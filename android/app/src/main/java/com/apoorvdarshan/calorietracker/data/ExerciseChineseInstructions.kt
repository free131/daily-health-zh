package com.apoorvdarshan.calorietracker.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

/** Local asset only; initialized before the exercise repository is exposed to the UI. */
object ExerciseChineseInstructions {
    @Volatile private var translations: Map<String, String> = emptyMap()

    internal fun parse(reader: Reader): Map<String, String> =
        Gson().fromJson(reader, object : TypeToken<Map<String, String>>() {}.type)

    internal fun load(context: Context) {
        if (translations.isNotEmpty()) return
        translations = context.assets.open("exercise-instructions-zh.json")
            .bufferedReader(Charsets.UTF_8).use { parse(it) }
    }

    fun translated(value: String): String? = translations[value.trim()]
}
