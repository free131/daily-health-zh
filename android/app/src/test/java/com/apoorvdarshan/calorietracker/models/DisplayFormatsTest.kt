package com.apoorvdarshan.calorietracker.models
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.DayOfWeek
import java.util.Locale

class DisplayFormatsTest {
    @Test fun chineseDatesDoNotDependOnEnglishMonthOrdering() {
        val d = LocalDate.of(2026, 9, 30)
        assertEquals("2026年9月30日", d.format(DisplayFormats.date("MMM d, yyyy", Locale.SIMPLIFIED_CHINESE)))
        assertEquals("9月30日", d.format(DisplayFormats.date("MMM d", Locale.SIMPLIFIED_CHINESE)))
        assertEquals("三", DisplayFormats.weekday(DayOfWeek.WEDNESDAY, Locale.SIMPLIFIED_CHINESE))
        assertEquals("Sep 30", d.format(DisplayFormats.date("MMM d", Locale.US)))
    }
    @Test fun decimalInputSupportsChineseKeyboardAndRejectsNonFiniteOrMalformedValues() {
        assertEquals(12.5, UserNumberInput.decimal("１２．５")!!, 0.00001)
        assertEquals(0.5, UserNumberInput.decimal("０，５")!!, 0.00001)
        assertEquals(12.5, ServingUnitOption.parseQuantity("１２．５")!!, 0.00001)
        assertEquals(3.0, ServingAmountExpression.evaluate("１．５×２")!!, 0.00001)
        listOf("NaN", "Infinity", "1.2.3", "1,000,000", "1kg", "1e999").forEach { assertNull(it, UserNumberInput.decimal(it)) }
        assertNull(ServingUnitOption.parseQuantity("Infinity"))
    }

    @Test fun localWorkoutQuestionsAreChineseButPreserveActivityName() {
        val old = Locale.getDefault()
        try {
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE)
            val draft = WorkoutTextDraft(LocalDate.now().toString(), listOf(
                WorkoutTextExercise(exerciseId = null, name = "My Lift")))
            val error = assertThrows(WorkoutClarification::class.java) { draft.planned(emptyList()) }
            assertEquals("「My Lift」做了几组，每组几次？", error.message)
            val aiReply = WorkoutClarification("Original provider question", listOf("Original option"))
            assertEquals("Original provider question", aiReply.message)
            assertEquals(listOf("Original option"), aiReply.options)
        } finally { Locale.setDefault(old) }
    }

    @Test fun waterInputDoesNotSilentlyRemoveTheDecimalPoint() {
        var typed = ""
        "250.5".forEach { typed = UserNumberInput.decimalEdit(typed + it, typed) }
        assertEquals("250.5", typed)
        assertEquals(251, WaterUnit.MILLILITERS.toMilliliters(UserNumberInput.decimal(typed)!!))
        assertEquals("250.5", UserNumberInput.decimalEdit("２５０，５", ""))
        assertEquals("250.5", UserNumberInput.decimalEdit("250.5.5", "250.5"))
        assertEquals("", UserNumberInput.decimalEdit("", "250.5"))
    }

    @Test fun waterAndWorkoutUnitsKeepTheirConversionRatios() {
        assertEquals(30, WaterUnit.FLUID_OUNCES.toMilliliters(1.0))
        assertEquals(250, WaterUnit.MILLILITERS.toMilliliters(250.0))
        assertEquals(1.0, WaterUnit.FLUID_OUNCES.displayAmount(30), 0.02)
        assertEquals("7.5", WorkoutRpeScale.STRENGTH.sanitize("７．５"))
    }
}
