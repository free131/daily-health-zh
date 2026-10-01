package com.apoorvdarshan.calorietracker.models

import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** UI dates only. Persistence, protocol dates and export schemas remain ISO. */
object DisplayFormats {
    fun date(pattern: String, locale: Locale = Locale.getDefault()): DateTimeFormatter {
        val localized = if (locale.language == "zh") pattern
            .replace("EEE, MMM d", "M月d日 EEEE")
            .replace("MMM d, yyyy", "yyyy年M月d日")
            .replace("d MMM yyyy", "yyyy年M月d日")
            .replace("MMM yyyy", "yyyy年M月")
            .replace("MMM d", "M月d日") else pattern
        return DateTimeFormatter.ofPattern(localized, locale)
    }

    fun weekday(day: DayOfWeek, locale: Locale = Locale.getDefault()): String =
        if (locale.language == "zh") listOf("一", "二", "三", "四", "五", "六", "日")[day.value - 1]
        else day.getDisplayName(TextStyle.NARROW, locale)
}
