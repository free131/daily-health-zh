package com.apoorvdarshan.calorietracker.services.speech

import com.apoorvdarshan.calorietracker.data.KeyStore
import com.apoorvdarshan.calorietracker.data.PreferencesStore
import com.apoorvdarshan.calorietracker.models.SpeechProvider
import com.apoorvdarshan.calorietracker.services.ai.FoodAnalysisService
import com.apoorvdarshan.calorietracker.services.ondevice.LocalWhisperRuntime
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import java.io.File

/**
 * Routes a single-shot recording to the selected local or remote STT provider.
 * Native on-device STT is handled separately via [NativeSpeechRecognizer] since it
 * streams partial results rather than taking a file upload.
 */
class SpeechService(
    private val prefs: PreferencesStore,
    private val keyStore: KeyStore,
    private val okHttp: OkHttpClient = FoodAnalysisService.defaultClient,
    private val localWhisper: LocalWhisperRuntime? = null
) {

    suspend fun transcribeRecordedAudio(audio: File): String =
        error("此版本已移除语音识别，请使用文字或照片。")
}
