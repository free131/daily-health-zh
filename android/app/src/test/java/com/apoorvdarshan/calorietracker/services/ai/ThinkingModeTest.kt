package com.apoorvdarshan.calorietracker.services.ai

import com.apoorvdarshan.calorietracker.models.AIProvider
import com.apoorvdarshan.calorietracker.services.AiApiRequest
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test

class ThinkingModeTest {
    private val original = Json.parseToJsonElement("""{"max_tokens":1024,"messages":[{"role":"user","content":"苹果"}]}""").jsonObject
    private fun apply(provider: AIProvider, model: String) = ThinkingMode.apply(original, provider, model, true)

    @Test fun disabledPreservesNormalRequestAndBudget() {
        assertEquals(original, ThinkingMode.apply(original, AIProvider.DEEPSEEK, "deepseek-chat", false))
    }
    @Test fun unsupportedModelsAndCustomEndpointsRemainUntouched() {
        listOf(AIProvider.OPENAI to "gpt-4o", AIProvider.GEMINI to "gemini-2.0-flash",
            AIProvider.GEMINI to "gemini-3-pro-image-preview", AIProvider.CUSTOM_OPENAI to "unknown",
            AIProvider.ANTHROPIC to "claude-3-haiku-20240307").forEach { (provider, model) ->
            assertEquals(original, apply(provider, model))
        }
    }
    @Test fun deepseekEnablesThinkingAndReservesBudget() {
        val body = apply(AIProvider.DEEPSEEK, "deepseek-chat")
        assertEquals("enabled", body["thinking"]!!.jsonObject["type"]!!.jsonPrimitive.content)
        assertEquals(8192, body["max_tokens"]!!.jsonPrimitive.int)
        assertEquals(original["messages"], body["messages"])
    }
    @Test fun openaiUsesCompletionBudgetAndEffort() {
        val body = apply(AIProvider.OPENAI, "gpt-5-mini")
        assertEquals("medium", body["reasoning_effort"]!!.jsonPrimitive.content)
        assertEquals(8192, body["max_completion_tokens"]!!.jsonPrimitive.int)
    }
    @Test fun geminiUsesVersionSpecificThinkingParameterAndPreservesJsonMode() {
        val input = Json.parseToJsonElement("""{"generationConfig":{"responseMimeType":"application/json","thinkingConfig":{"thinkingLevel":"minimal"}}}""").jsonObject
        for (model in listOf("gemini-2.5-flash", "gemini-3-flash-preview")) {
            val config = ThinkingMode.apply(input, AIProvider.GEMINI, model, true)["generationConfig"]!!.jsonObject
            assertEquals("application/json", config["responseMimeType"]!!.jsonPrimitive.content)
            val thinking = config["thinkingConfig"]!!.jsonObject
            assertEquals(1, thinking.size)
            if (model.contains("2.5")) assertEquals(2048, thinking["thinkingBudget"]!!.jsonPrimitive.int)
            else assertEquals("high", thinking["thinkingLevel"]!!.jsonPrimitive.content)
        }
    }
    @Test fun claudeAdaptiveAndManualModesHaveCompatibleBudgets() {
        assertEquals("adaptive", apply(AIProvider.ANTHROPIC, "claude-sonnet-4-6")["thinking"]!!.jsonObject["type"]!!.jsonPrimitive.content)
        val old = apply(AIProvider.ANTHROPIC, "claude-sonnet-4-5-20250929")
        assertTrue(old["thinking"]!!.jsonObject["budget_tokens"]!!.jsonPrimitive.int < old["max_tokens"]!!.jsonPrimitive.int)
    }
    @Test fun openrouterNoneCannotDefeatEnabledSwitchAndExplicitEffortIsPreserved() {
        for (effort in listOf("none", "high")) {
            val body = JsonObject(original + ("reasoning" to buildJsonObject { put("effort", effort); put("exclude", false) }))
            val result = ThinkingMode.apply(body, AIProvider.OPENROUTER, "provider/model", true)["reasoning"]!!.jsonObject
            assertEquals(if (effort == "none") "medium" else "high", result["effort"]!!.jsonPrimitive.content)
            assertEquals(false, result["exclude"]!!.jsonPrimitive.boolean)
        }
    }
    @Test fun doesNotReduceLargerUserBudget() {
        val body = JsonObject(original + ("max_tokens" to JsonPrimitive(12000)))
        assertEquals(12000, ThinkingMode.apply(body, AIProvider.DEEPSEEK, "deepseek-chat", true)["max_tokens"]!!.jsonPrimitive.int)
    }
    @Test fun actualHttpRequestEnablesThinkingAndRetainsToolContinuation() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("{}"))
            val input = """{"model":"deepseek-chat","thinking":{"type":"disabled"},"max_tokens":1024,"messages":[{"role":"assistant","content":null,"reasoning_content":"synthetic test reasoning","tool_calls":[{"id":"qa"}]}],"tools":[{"type":"function"}]}"""
            var tagged = false
            val base = OkHttpClient.Builder().addInterceptor { chain ->
                tagged = chain.request().tag(AiApiRequest::class.java) != null
                chain.proceed(chain.request())
            }.build()
            val request = Request.Builder().url(server.url("/chat/completions"))
                .tag(AiApiRequest::class.java, AiApiRequest)
                .post(input.toRequestBody("application/json".toMediaType())).build()
            ThinkingMode.client(base, AIProvider.DEEPSEEK, "deepseek-chat", true).newCall(request).execute().close()
            val sent = Json.parseToJsonElement(server.takeRequest().body.readUtf8()).jsonObject
            assertTrue(tagged)
            assertEquals("enabled", sent["thinking"]!!.jsonObject["type"]!!.jsonPrimitive.content)
            assertEquals(Json.parseToJsonElement(input).jsonObject["messages"], sent["messages"])
            assertTrue("tools" in sent)
        }
    }
}
