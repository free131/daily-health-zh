package com.apoorvdarshan.calorietracker.ui.settings

import com.apoorvdarshan.calorietracker.ui.about.AboutSettingsCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsCategoryTest {
    @Test
    fun hubKeepsEveryFocusedCategory() {
        assertEquals(15, SettingsCategory.entries.size)
        assertEquals(15, SettingsCategory.entries.map { it.titleRes }.toSet().size)
        assertEquals(9, SettingsCategory.preferenceEntries.size)
        assertEquals(false, SettingsCategory.SPEECH_TO_TEXT in SettingsCategory.preferenceEntries)
        assertEquals(listOf(SettingsCategory.LEGAL), SettingsCategory.appInfoEntries)
        assertEquals(5, SettingsCategory.entries.mapNotNull { it.aboutCategory }.size)
    }

    @Test
    fun appInfoKeepsEveryFocusedCategory() {
        assertEquals(5, AboutSettingsCategory.entries.size)
        assertEquals(5, AboutSettingsCategory.entries.map { it.titleRes }.toSet().size)
    }
}
