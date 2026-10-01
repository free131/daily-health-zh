package com.apoorvdarshan.calorietracker.ui.home

import android.content.Context
import com.apoorvdarshan.calorietracker.models.HomeGreeting
import com.apoorvdarshan.calorietracker.models.HomeGreetings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

/** One greeting per foreground visit; changing tabs or recomposing never advances it. */
object HomeGreetingSession {
    private val state = MutableStateFlow<HomeGreeting?>(null)
    val current = state.asStateFlow()

    fun open(context: Context, restoringActivity: Boolean) {
        if (restoringActivity && state.value != null) return
        val prefs = context.getSharedPreferences("home_greeting_rotation", Context.MODE_PRIVATE)
        val now = LocalDateTime.now()
        val selected = HomeGreetings.next(now.toLocalDate(), now.hour, prefs.getString("previous_id", null))
        state.value = selected
        prefs.edit().putString("previous_id", selected.id).apply()
    }
}
