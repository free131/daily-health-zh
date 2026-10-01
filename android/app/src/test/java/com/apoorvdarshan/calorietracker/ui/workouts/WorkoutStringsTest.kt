package com.apoorvdarshan.calorietracker.ui.workouts

import com.apoorvdarshan.calorietracker.models.WorkoutSplit
import com.apoorvdarshan.calorietracker.models.WorkoutSplitGroup
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class WorkoutStringsTest {
    private fun withLocale(locale: Locale, block: () -> Unit) {
        val previous = Locale.getDefault()
        try { Locale.setDefault(locale); block() } finally { Locale.setDefault(previous) }
    }
    @Test fun screenshotLabelsAndMusclesAreChinese() = withLocale(Locale.SIMPLIFIED_CHINESE) {
        val expected = mapOf("Calculate" to "计算", "CALORIE BURN" to "热量消耗",
            "Sets" to "组数", "Workouts" to "训练动作", "Reps" to "次数", "Burn" to "消耗",
            "Today" to "今天", "No workouts logged" to "还没有训练记录",
            "Abdominals" to "腹肌", "Abductors" to "髋外展肌群", "Lower Back" to "下背部")
        expected.forEach { (english, chinese) -> assertEquals(chinese, WorkoutStrings.text(english)) }
    }
    @Test fun dynamicLabelsAndCombinedMetadataAreChinese() = withLocale(Locale.SIMPLIFIED_CHINESE) {
        assertEquals("3 个动作", WorkoutStrings.text("3 workouts"))
        assertEquals("第 2 组", WorkoutStrings.text("Set 2"))
        assertEquals("胸肌, 肱三头肌 · 杠铃", WorkoutStrings.text("Chest, Triceps · Barbell"))
        assertEquals("点击＋，为当天添加全身训练动作", WorkoutStrings.text("Use + to pick Full body exercises for this day"))
        assertEquals("上次：12 次", WorkoutStrings.text("Last time: 12 reps"))
    }
    @Test fun translatingDoesNotChangeStoredFilterKeys() = withLocale(Locale.SIMPLIFIED_CHINESE) {
        val groups = WorkoutSplitGroup.groups(WorkoutSplit.BODY_PART, listOf("Chest", "Biceps"))
        val chest = groups.first { it.title == "Chest" }
        assertEquals("胸肌", WorkoutStrings.text(chest.title))
        assertEquals(setOf("Chest"), chest.muscles)
        assertEquals("Chest", chest.title)
    }
    @Test fun switchingBackToEnglishAndCustomNamesWork() {
        withLocale(Locale.US) { assertEquals("Calculate", WorkoutStrings.text("Calculate")) }
        withLocale(Locale.SIMPLIFIED_CHINESE) { assertEquals("我的动作 A", WorkoutStrings.text("我的动作 A")) }
    }
}
