package com.apoorvdarshan.calorietracker.export

import com.apoorvdarshan.calorietracker.models.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

class ChineseDiaryExportTest {
    @Test fun chineseMarkdownPreservesUserTextAndJsonRoundTripsUnchanged() {
        val old = Locale.getDefault()
        try {
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE)
            val entry = FoodEntry(name = "My Food Calories", calories = 123, protein = 1.5, carbs = 2.5, fat = 3.5,
                timestamp = Instant.parse("2026-09-30T04:00:00Z"), mealType = MealType.LUNCH, source = FoodSource.MANUAL)
            val date = entry.timestamp.atZone(ZoneId.systemDefault()).toLocalDate()
            fun export(format: DiaryFormat) = DiaryExporter.build(listOf(entry), date, date, format, null, { "午餐" })!!.second
            val markdown = export(DiaryFormat.MARKDOWN)
            assertTrue(markdown.startsWith("# 饮食日记导出"))
            assertTrue(markdown.contains("My Food Calories"))
            assertTrue(markdown.contains("手动记录"))
            assertFalse(markdown.contains("| Time |"))
            val imported = DiaryImporter.parse(export(DiaryFormat.JSON)).entries.single()
            assertEquals(entry.name, imported.name)
            assertEquals(entry.id, imported.id)
            assertEquals(1.5, imported.protein, 0.00001)
            assertTrue(export(DiaryFormat.CSV).startsWith("date,meal,time,food,"))
        } finally { Locale.setDefault(old) }
    }
}
