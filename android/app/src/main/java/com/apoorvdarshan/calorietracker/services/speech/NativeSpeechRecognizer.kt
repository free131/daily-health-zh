package com.apoorvdarshan.calorietracker.services.speech

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed class SttEvent {
    data class Partial(val text: String) : SttEvent()
    data class Final(val text: String) : SttEvent()
    data class Error(val code: Int, val message: String) : SttEvent()
    object Ready : SttEvent()
    object EndOfSpeech : SttEvent()
}

/**
 * Wraps Android's [SpeechRecognizer] as a cold Flow. Emits live partials while
 * the user speaks, then a Final event on completion. Port of iOS native-iOS STT
 * one-tap flow.
 */
class NativeSpeechRecognizer(private val context: Context) {

    companion object {
        private const val ERROR_SERVER_DISCONNECTED = 11
        private const val ERROR_LANGUAGE_NOT_SUPPORTED = 12
        private const val ERROR_LANGUAGE_UNAVAILABLE = 13

        fun isRecoverableSessionError(code: Int): Boolean =
            code == SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
                    code == SpeechRecognizer.ERROR_NO_MATCH ||
                    code == SpeechRecognizer.ERROR_RECOGNIZER_BUSY ||
                    code == ERROR_SERVER_DISCONNECTED

        fun isLanguageSupportError(code: Int): Boolean =
            code == ERROR_LANGUAGE_NOT_SUPPORTED || code == ERROR_LANGUAGE_UNAVAILABLE
    }

    fun isAvailable(): Boolean = false

    fun hasMicPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED

    fun listen(locale: String? = null, preferOffline: Boolean = true): Flow<SttEvent> = callbackFlow {
        trySend(SttEvent.Error(5, "此版本已移除语音识别，请使用文字或照片。"))
        close()
    }

    private fun describeError(code: Int): String = when (code) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio capture failed"
        SpeechRecognizer.ERROR_CLIENT -> "Client error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Missing microphone permission"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
        SpeechRecognizer.ERROR_SERVER -> "Server error"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
        ERROR_SERVER_DISCONNECTED -> "Speech service disconnected"
        ERROR_LANGUAGE_NOT_SUPPORTED -> "Speech language is not supported on this device"
        ERROR_LANGUAGE_UNAVAILABLE -> "Speech language is unavailable on this device"
        else -> "Speech error ($code)"
    }
}
