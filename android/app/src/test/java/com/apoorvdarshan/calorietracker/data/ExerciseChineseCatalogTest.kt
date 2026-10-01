package com.apoorvdarshan.calorietracker.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.apoorvdarshan.calorietracker.ui.workouts.WorkoutStrings
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.Locale

class ExerciseChineseCatalogTest {
    private val records: List<ExerciseRecord> by lazy {
        File("../../ios/calorietracker/Resources/FreeExerciseDB/dist/exercises.json")
            .reader(Charsets.UTF_8).use {
                Gson().fromJson(it, object : TypeToken<List<ExerciseRecord>>() {}.type)
            }
    }

    private fun inChinese(block: () -> Unit) {
        val old = Locale.getDefault()
        try { Locale.setDefault(Locale.SIMPLIFIED_CHINESE); block() }
        finally { Locale.setDefault(old) }
    }

    @Test fun everyBundledNameHasAChineseDisplayAndRetainsItsIdentity() = inChinese {
        assertEquals(records.map { it.id }.toSet(), ExerciseChineseNames.byId.keys)
        assertEquals(records.map { it.name }.toSet(), ExerciseChineseNames.byEnglish.keys)
        records.forEach { record ->
            val item = requireNotNull(ExerciseItem.from(record))
            val translated = ExerciseDisplay.name(item.id, item.name)
            assertTrue(record.name, translated.contains(Regex("[\\u4e00-\\u9fff]")))
            assertEquals(ExerciseChineseNames.byId[item.id], translated)
            assertEquals(record.name, item.name)
            assertEquals(record.id, item.id)
        }
    }

    @Test fun allNonemptyInstructionStepsAreBundledAndNotEnglishFallbacks() {
        val translations = File("src/main/assets/exercise-instructions-zh.json")
            .reader(Charsets.UTF_8).use { ExerciseChineseInstructions.parse(it) }
        val expected = records.flatMap { it.instructions }.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        assertEquals(expected, translations.keys)
        translations.forEach { (english, chinese) ->
            assertTrue(english, chinese.contains(Regex("[\\u4e00-\\u9fff]")))
            assertFalse(english, chinese.contains("[["))
            assertFalse(english, chinese.contains("清洁"))
        }
    }

    @Test fun screenshotNamesAndChineseSearchWorkWithExistingEnglishRecords() = inChinese {
        val expected = mapOf("Alternating Kettlebell Row" to "交替壶铃划船",
            "Alternating Renegade Row" to "交替支撑划船",
            "Anti-Gravity Press" to "俯卧上斜杠铃前推",
            "Barbell Rear Delt Row" to "杠铃肩后束划船")
        expected.forEach { (english, chinese) ->
            val item = ExerciseItem.from(records.first { it.name == english })!!
            assertEquals(chinese, ExerciseDisplay.name(item.id, item.name))
            assertTrue(ExerciseSearch.matches(item.searchableText, chinese, item.id))
            assertTrue(ExerciseSearch.matches(item.searchableText, english, item.id))
        }
        val item = ExerciseItem.from(records.first { it.name == "Alternating Kettlebell Row" })!!
        assertTrue(ExerciseSearch.matches(item.searchableText, "壶铃 划船", item.id))
        assertFalse(ExerciseSearch.matches(item.searchableText, "深蹲", item.id))
        assertEquals("开始计时：交替壶铃划船", WorkoutStrings.text("Start timer for ${ExerciseDisplay.name(item.id, item.name)}"))
        assertEquals("预览：交替壶铃划船", WorkoutStrings.text("Preview ${ExerciseDisplay.name(item.id, item.name)}"))
    }

    @Test fun englishModeAndUserDefinedTextAreNotRewritten() {
        val old = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("Alternating Kettlebell Row", WorkoutStrings.text("Alternating Kettlebell Row"))
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE)
            assertEquals("我的划船 A", WorkoutStrings.text("我的划船 A"))
            assertEquals("Custom lift 123", WorkoutStrings.text("Custom lift 123"))
        } finally { Locale.setDefault(old) }
    }
}
