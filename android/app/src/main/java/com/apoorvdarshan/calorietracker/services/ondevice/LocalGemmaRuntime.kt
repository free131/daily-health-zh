package com.apoorvdarshan.calorietracker.services.ondevice
import android.content.Context
class LocalGemmaRuntime(context: Context, models: LocalModelManager) {
    fun isReady() = false
    suspend fun generate(prompt: String, images: List<ByteArray> = emptyList(), maxOutputTokens: Int, systemInstruction: String? = null): String = error("请配置 AI API。")
    suspend fun close() = Unit
}
