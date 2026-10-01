package com.apoorvdarshan.calorietracker.services.ai
import java.util.Locale
internal object ResponseLanguage {
    fun instruction(): String = if (Locale.getDefault().language == "zh")
        "\n\nUse Simplified Chinese for food names, ingredient names, explanations, advice, and all user-facing text. Keep JSON keys, enum values, canonical serving unit codes, model IDs and numbers unchanged. Do not append English plural suffixes to Chinese words."
        else ""
}
