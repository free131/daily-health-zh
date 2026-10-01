package com.apoorvdarshan.calorietracker.services.ondevice
import android.content.Context
import java.io.File
class LocalWhisperRuntime(context: Context, models: LocalModelManager) {
    fun isReady() = false
    suspend fun transcribe(audio: File, languageCode: String?): String = error("此版本已移除语音识别。")
    suspend fun close() = Unit
}
