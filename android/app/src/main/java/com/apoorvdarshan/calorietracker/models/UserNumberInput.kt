package com.apoorvdarshan.calorietracker.models

import java.text.Normalizer

/** Normalize only numeric input fields, never names, notes, IDs or serialized data. */
object UserNumberInput {
    fun normalize(value: String): String = Normalizer.normalize(value.trim(), Normalizer.Form.NFKC)
        .replace('−', '-')

    /** Preserve decimal separators while editing; reject invalid edits instead of changing their value. */
    fun decimalEdit(value: String, previous: String): String {
        val normalized = normalize(value).replace(',', '.')
        return if (Regex("[0-9]{0,9}(?:\\.[0-9]{0,3})?").matches(normalized)) normalized else previous
    }

    fun decimal(value: String): Double? {
        val normalized = normalize(value).replace(',', '.')
        if (!Regex("[+-]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)").matches(normalized)) return null
        return normalized.toDoubleOrNull()?.takeIf { it.isFinite() }
    }
}
