package com.apoorvdarshan.calorietracker.services.ai

import com.apoorvdarshan.calorietracker.models.AIProvider
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.util.concurrent.TimeUnit

/** Applied at the request boundary, including retries and tool continuations. */
internal object ThinkingMode {
    fun supported(provider: AIProvider, model: String): Boolean = when (provider) {
        AIProvider.DEEPSEEK, AIProvider.OPENROUTER -> true
        AIProvider.OPENAI -> Regex("^(gpt-5(?!.*(?:chat|pro))|o[134](?!.*pro)).*").matches(model)
        AIProvider.GEMINI -> !model.contains("image") && !model.contains("tts") &&
            (model.startsWith("gemini-3") || model.startsWith("gemini-2.5"))
        AIProvider.ANTHROPIC -> model.startsWith("claude-") &&
            (model.contains("3-7") || Regex(".*(?:opus|sonnet|haiku)-4(?:-|$).*|claude-4-.*").matches(model))
        else -> false
    }

    fun apply(body: JsonObject, provider: AIProvider, model: String, enabled: Boolean): JsonObject {
        if (!enabled || !supported(provider, model)) return body
        val fields = body.toMutableMap()
        val budgetKey = if (provider == AIProvider.OPENAI) "max_completion_tokens" else "max_tokens"
        if (provider != AIProvider.GEMINI) {
            fields[budgetKey] = JsonPrimitive(maxOf(body[budgetKey]?.jsonPrimitive?.intOrNull ?: 1024, 8192))
        }
        when (provider) {
            AIProvider.DEEPSEEK -> fields["thinking"] = buildJsonObject { put("type", "enabled") }
            AIProvider.OPENAI -> fields["reasoning_effort"] = JsonPrimitive("medium")
            AIProvider.OPENROUTER -> fields["reasoning"] = buildJsonObject {
                // Preserve explicit effort, but AUTO/NONE must not defeat this switch.
                val effort = body["reasoning"]?.jsonObject?.get("effort")?.jsonPrimitive?.contentOrNull
                put("effort", effort?.takeUnless { it == "none" || it == "auto" } ?: "medium")
                body["reasoning"]?.jsonObject?.get("exclude")?.let { put("exclude", it) }
            }
            AIProvider.GEMINI -> fields["generationConfig"] = buildJsonObject {
                body["generationConfig"]?.jsonObject?.forEach { (k, v) -> if (k != "thinkingConfig") put(k, v) }
                put("maxOutputTokens", maxOf(body["generationConfig"]?.jsonObject?.get("maxOutputTokens")?.jsonPrimitive?.intOrNull ?: 1024, 8192))
                putJsonObject("thinkingConfig") {
                    if (model.startsWith("gemini-2.5")) put("thinkingBudget", 2048)
                    else put("thinkingLevel", "high")
                }
            }
            AIProvider.ANTHROPIC -> fields["thinking"] = buildJsonObject {
                if (Regex(".*(?:opus|sonnet)-4-[6-9](?:-|$).*").matches(model) ||
                    Regex(".*(?:opus|sonnet)-4-[6-9]$").matches(model)) put("type", "adaptive")
                else { put("type", "enabled"); put("budget_tokens", 2048) }
            }
            else -> Unit
        }
        return JsonObject(fields)
    }

    fun client(client: OkHttpClient, provider: AIProvider, model: String, enabled: Boolean): OkHttpClient {
        if (!enabled || !supported(provider, model)) return client
        return client.newBuilder()
            .callTimeout(maxOf(client.callTimeoutMillis.toLong(), 180_000), TimeUnit.MILLISECONDS)
            .readTimeout(maxOf(client.readTimeoutMillis.toLong(), 180_000), TimeUnit.MILLISECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val body = request.body
                if (body == null || request.method != "POST") chain.proceed(request)
                else {
                    val buffer = Buffer()
                    body.writeTo(buffer)
                    val payload = Json.parseToJsonElement(buffer.readUtf8()).jsonObject
                    chain.proceed(request.newBuilder().post(
                        apply(payload, provider, model, enabled).toString().toRequestBody(body.contentType())
                    ).build())
                }
            }.build()
    }
}
