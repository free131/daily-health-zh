package com.apoorvdarshan.calorietracker.models

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MealRecommendationTest {
    private fun response(count: Int = 3, calories: Int = 500, meals: Int = 1): String =
        """{"summary":"按已有记录安排后续饮食","plans":[${(1..count).joinToString { i ->
            """{"title":"方案$i","reason":"方便准备","meals":[${(1..meals).joinToString {
                """{"name":"晚餐","foods":"熟米饭100克、蒸鱼150克","calories":$calories}"""
            }}]}"""
        }}]}"""
    @Test fun threeMealOptionsAndSum() {
        val result = MealRecommendations.parse(response(), false)
        assertEquals(3, result.plans.size)
        assertEquals(500, result.plans[0].calories)
    }
    @Test fun wholeDayAcceptsSeveralMealsAndComputesTotal() {
        assertEquals(1500, MealRecommendations.parse(response(meals = 3), true).plans[0].calories)
    }
    @Test fun fencedJsonIsAccepted() {
        assertEquals(3, MealRecommendations.parse("```json\n${response()}\n```", false).plans.size)
    }
    @Test fun missingOptionsAreNotFabricated() {
        assertThrows(IllegalArgumentException::class.java) { MealRecommendations.parse(response(count = 2), false) }
    }
    @Test fun sameTitlesAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { MealRecommendations.parse(response().replace("方案2", "方案1"), false) }
    }
    @Test fun mealModeRejectsWholeDayResponse() {
        assertThrows(IllegalArgumentException::class.java) { MealRecommendations.parse(response(meals = 3), false) }
    }
    @Test fun invalidEstimatesAreRejected() {
        for (kcal in listOf(0, -1, 10001)) assertThrows(IllegalArgumentException::class.java) {
            MealRecommendations.parse(response(calories = kcal), false)
        }
    }
    @Test fun truncatedResponseFailsWithoutFakeOptions() {
        assertThrows(IllegalArgumentException::class.java) { MealRecommendations.parse(response().dropLast(12), true) }
    }
    @Test fun emptyFoodDescriptionIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            MealRecommendations.parse(response().replace("熟米饭100克、蒸鱼150克", ""), false)
        }
    }
    @Test fun localDateBoundaryExcludesYesterdayAndTomorrow() {
        fun food(time: String) = FoodEntry(name="测试", calories=100, protein=1.0, carbs=1.0, fat=1.0,
            source=FoodSource.MANUAL, timestamp=Instant.parse(time))
        val entries = listOf(food("2026-09-30T15:59:59Z"),food("2026-09-30T16:00:00Z"),
            food("2026-10-01T15:59:59Z"),food("2026-10-01T16:00:00Z"))
        val today = MealRecommendations.todayFoods(entries, LocalDate.parse("2026-10-01"), ZoneId.of("Asia/Shanghai"))
        assertEquals(entries.subList(1,3), today)
    }
}
