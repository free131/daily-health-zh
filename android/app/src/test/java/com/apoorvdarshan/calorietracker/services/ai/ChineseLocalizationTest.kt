package com.apoorvdarshan.calorietracker.services.ai

import com.apoorvdarshan.calorietracker.models.ServingUnitOption
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class ChineseLocalizationTest {
    private fun inLocale(locale: Locale, body: () -> Unit) {
        val original = Locale.getDefault()
        try { Locale.setDefault(locale); body() } finally { Locale.setDefault(original) }
    }
    @Test fun chineseUnitsHaveNoEnglishPluralSuffix() = inLocale(Locale.SIMPLIFIED_CHINESE) {
        assertEquals("克", ServingUnitOption.grams.displayUnit(200.0))
        assertEquals("份", ServingUnitOption.loggedServing().displayUnit(2.0))
        assertEquals("碗", ServingUnitOption("bowl", 200.0).displayUnit(2.0))
        assertEquals("个", ServingUnitOption("个", 100.0).displayUnit(2.0))
    }
    @Test fun aiKeepsSchemaButUsesChineseForVisibleText() = inLocale(Locale.SIMPLIFIED_CHINESE) {
        assertTrue(ResponseLanguage.instruction().contains("简体中文"))
        assertTrue(ResponseLanguage.instruction().contains("JSON 键"))
    }
    @Test fun englishLocaleStillUsesEnglishUnits() = inLocale(Locale.US) {
        assertEquals("pieces", ServingUnitOption("piece", 100.0).displayUnit(2.0))
        assertTrue(ResponseLanguage.instruction().contains("简体中文"))
    }
}
