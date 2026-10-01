package com.apoorvdarshan.calorietracker.models
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test
class HomeGreetingsTest {
    @Test fun holidaysUseActualLunarDates() {
        assertEquals("春节", HomeGreetings.festival(LocalDate.parse("2026-02-17")))
        assertEquals("除夕", HomeGreetings.festival(LocalDate.parse("2026-02-16")))
        assertEquals("中秋", HomeGreetings.festival(LocalDate.parse("2026-09-25")))
        assertEquals("端午", HomeGreetings.festival(LocalDate.parse("2026-06-19")))
        assertNull(HomeGreetings.festival(LocalDate.parse("2026-06-18")))
    }
    @Test fun coldDewIsNotHardcodedToEveryOctoberEighth() {
        assertEquals("寒露", GreetingCalendar.solarTerms["2026-10-08"])
        assertNull(GreetingCalendar.solarTerms["2026-10-07"])
        assertEquals(120, GreetingCalendar.solarTerms.size)
        assertEquals(35, GreetingCalendar.lunarFestivals.size)
    }
    @Test fun repeatedLaunchesRotateAndWrapWithoutAdjacentDuplicates() {
        for (day in listOf("2026-10-01", "2026-10-08", "2026-10-10", "2026-10-12")) {
            val date = LocalDate.parse(day)
            var previous: String? = null
            repeat(30) {
                val next = HomeGreetings.next(date, 9, previous)
                assertNotEquals(previous, next.id)
                assertTrue(next.chinese.isNotBlank())
                previous = next.id
            }
        }
    }
    @Test fun outOfRangeDoesNotInventSolarOrLunarDates() {
        assertNull(GreetingCalendar.solarTerms["2040-10-08"])
        assertTrue(HomeGreetings.next(LocalDate.parse("2040-10-08"), 14, null).id.startsWith("daily:"))
    }
    @Test fun morningEveningAndWeekendHaveAppropriateChoices() {
        val day=LocalDate.parse("2026-10-10")
        assertTrue(HomeGreetings.candidates(day, 8).any { "早" in it.chinese })
        assertTrue(HomeGreetings.candidates(day, 20).any { "晚上" in it.chinese })
        assertTrue(HomeGreetings.candidates(day, 14).any { "周末" in it.chinese })
    }
}
