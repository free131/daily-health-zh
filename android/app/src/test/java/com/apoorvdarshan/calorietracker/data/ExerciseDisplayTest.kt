package com.apoorvdarshan.calorietracker.data
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class ExerciseDisplayTest {
    @Test fun customAndRenamedExercisesKeepUserTextEvenWhenItMatchesDictionary() {
        val zh = Locale.SIMPLIFIED_CHINESE
        val name = "Alternating Kettlebell Row"
        assertEquals(name, ExerciseDisplay.name("user-123", name, zh))
        assertEquals("Chest", ExerciseDisplay.name("user-123", "Chest", zh))
        assertEquals("我的动作", ExerciseDisplay.name("Alternating_Kettlebell_Row", "我的动作", zh))
        assertEquals("交替壶铃划船", ExerciseDisplay.name("Alternating_Kettlebell_Row", name, zh))
        assertEquals(name, ExerciseDisplay.name("Alternating_Kettlebell_Row", name, Locale.US))
    }
}
