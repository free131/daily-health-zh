package com.apoorvdarshan.calorietracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apoorvdarshan.calorietracker.AppContainer
import com.apoorvdarshan.calorietracker.R
import com.apoorvdarshan.calorietracker.models.*
import com.apoorvdarshan.calorietracker.services.ai.AiError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import java.time.ZoneId

data class MealRecommendationState(
    val loading: Boolean = false,
    val result: MealRecommendation? = null,
    val errorRes: Int? = null,
    val generatedFor: String = "",
    val generatedAt: String = ""
)

class MealRecommendationViewModel(private val container: AppContainer) : ViewModel() {
    private val state = MutableStateFlow(MealRecommendationState())
    val ui = state.asStateFlow()
    private var request: Job? = null

    fun cancel() { request?.cancel(); state.value = state.value.copy(loading = false) }

    fun generate(wholeDay: Boolean, meal: MealType, preference: String) {
        if (state.value.loading) return
        state.value = MealRecommendationState(loading = true)
        request = viewModelScope.launch {
            try {
                val profile = container.profileRepository.current()
                    ?: return@launch run { state.value = MealRecommendationState(errorRes = R.string.coach_no_profile_error) }
                val now = LocalDateTime.now()
                val date = now.toLocalDate()
                val foods = MealRecommendations.todayFoods(container.foodRepository.entries.first(), date, ZoneId.systemDefault())
                val workouts = container.workoutRepository.snapshot()
                val scope = container.appContext.getString(if (wholeDay) R.string.recommend_day else meal.displayNameRes)
                val context = buildJsonObject {
                    put("date", date.toString()); put("localTime", now.toLocalTime().toString())
                    put("scope", scope); put("wholeDay", wholeDay)
                    put("age", profile.age); put("goal", profile.goal.name)
                    put("weightKg", profile.weightKg)
                    put("calorieGoal", profile.effectiveCalories)
                    put("proteinGoalG", profile.effectiveProtein); put("carbsGoalG", profile.effectiveCarbs); put("fatGoalG", profile.effectiveFat)
                    put("allergens", JsonArray(profile.allergenSensitivities.map(::JsonPrimitive)))
                    put("preferences", preference.take(500))
                    put("consumedCalories", foods.sumOf { it.calories })
                    put("remainingCalories", profile.effectiveCalories - foods.sumOf { it.calories })
                    put("consumedProteinG", foods.sumOf { it.protein }); put("consumedCarbsG", foods.sumOf { it.carbs }); put("consumedFatG", foods.sumOf { it.fat })
                    put("foods", buildJsonArray { foods.forEach { f -> add(buildJsonObject {
                        put("name", f.name); put("meal", f.mealType.name); put("calories", f.calories)
                    }) } })
                    put("plannedExercises", buildJsonArray { workouts.dayPlans[date.toString()]?.exercises?.forEach { add(it.name) } })
                    put("completedTrainingMinutes", workouts.completedSessions.filter { it.diaryDateKey == date.toString() }.sumOf { it.durationSeconds } / 60)
                    put("completedTrainingSessions", workouts.completedSessions.count { it.diaryDateKey == date.toString() })
                }
                val reply = container.chatService.sendMessage(
                    history = emptyList(), newUserMessage = context.toString(), profile = profile,
                    weights = emptyList(), bodyFats = emptyList(), foods = emptyList(),
                    heightMetric = true, weightMetric = true, systemPromptOverride = MealRecommendations.instruction,
                    responseTokenBudget = 3072, allowDataTools = false
                )
                ensureActive()
                val result = try { MealRecommendations.parse(reply, wholeDay) }
                    catch (e: Exception) { throw AiError.InvalidResponse }
                state.value = MealRecommendationState(result = result, generatedFor = scope,
                    generatedAt = now.format(java.time.format.DateTimeFormatter.ofPattern("M月d日 HH:mm")))
            } catch (e: CancellationException) { throw e }
            catch (e: AiError) { state.value = MealRecommendationState(errorRes = e.kind.messageRes) }
            catch (e: Exception) { state.value = MealRecommendationState(errorRes = R.string.ai_error_generic) }
        }
    }
    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MealRecommendationViewModel(container) as T
    }
}
