package com.apoorvdarshan.calorietracker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apoorvdarshan.calorietracker.AppContainer
import com.apoorvdarshan.calorietracker.R
import com.apoorvdarshan.calorietracker.ui.util.formattedWholeNumber
import com.apoorvdarshan.calorietracker.models.FoodLogMethod
import com.apoorvdarshan.calorietracker.models.MacroValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun DailyHomeHeader(date: LocalDate, recordsMode: Boolean, onSettings: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    val greeting by HomeGreetingSession.current.collectAsState()
    val pattern = if (locale.language == "zh") "M月d日 · EEEE" else "MMM d · EEEE"
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f)) {
            Text(date.format(DateTimeFormatter.ofPattern(pattern, locale)),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(if (recordsMode) stringResource(R.string.daily_records_title)
                else greeting?.let { if (locale.language == "zh") it.chinese else it.english }
                    ?: stringResource(R.string.daily_home_title),
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
        FilledTonalIconButton(onClick = onSettings,
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Icon(Icons.Filled.Settings, stringResource(R.string.nav_settings))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DailyNutritionCard(ui: HomeUiState, compact: Boolean, onDetails: () -> Unit) {
    val fontScale = LocalConfiguration.current.fontScale
    val goal = ui.profile?.effectiveCalories ?: 2000
    val current = ui.caloriesToday
    val difference = kotlin.math.abs(goal - current)
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(if (compact) R.string.daily_day_intake else R.string.daily_intake), style = MaterialTheme.typography.labelLarge)
                Text(stringResource(R.string.daily_goal, goal.formattedWholeNumber()),
                    color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            Text(stringResource(R.string.daily_calories, current.formattedWholeNumber()),
                fontSize = if (compact) 24.sp else 32.sp, fontWeight = FontWeight.SemiBold,
                lineHeight = if (compact) 30.sp else 40.sp)
            LinearProgressIndicator(progress = { if (goal > 0) (current.toFloat() / goal).coerceIn(0f, 1f) else 0f },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                gapSize = 0.dp, drawStopIndicator = {})
            FlowRow(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (goal <= 0) stringResource(R.string.daily_no_goal) else stringResource(
                    if (current > goal) R.string.daily_over else R.string.daily_remaining, difference.formattedWholeNumber()),
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = onDetails, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text(stringResource(R.string.daily_nutrition_details), style = MaterialTheme.typography.labelMedium)
                }
            }
            ui.homeBurnSummary?.let { burn ->
                Text(stringResource(R.string.daily_burn, burn.burnedCalories.formattedWholeNumber()),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!compact) {
                // Keep the user's nutrient choices. Wrap instead of squeezing four or five cards into one row.
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 4) {
                    ui.homeTopNutrients.take(4).forEach { nutrient ->
                        Column(Modifier.widthIn(min = (64 * fontScale).dp).weight(1f)) {
                            Text(stringResource(nutrient.displayNameRes), style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${MacroValueFormatter.string(nutrient.current(ui.todayEntries))} ${nutrient.unit}",
                                style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    if (ui.waterTrackingEnabled) {
                        Column(Modifier.widthIn(min = (64 * fontScale).dp).weight(1f)) {
                            Text(stringResource(R.string.water), style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(ui.waterUnit.format(ui.waterTodayMl), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DailyRecordActions(onCamera: () -> Unit, onDescription: () -> Unit, onManual: () -> Unit, onReuse: (FoodLogMethod) -> Unit) {
    var reuseExpanded by remember { mutableStateOf(false) }
    val actionWidth = (136 * LocalConfiguration.current.fontScale).dp
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.record_section), style = MaterialTheme.typography.titleSmall)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp), maxItemsInEachRow = 2) {
                Button(onClick = onCamera, modifier = Modifier.weight(1f).widthIn(min = actionWidth),
                    contentPadding = PaddingValues(16.dp), shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Filled.PhotoCamera, null, Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Column { Text(stringResource(R.string.daily_camera)); Text(stringResource(R.string.record_camera_hint), style = MaterialTheme.typography.bodySmall) }
                }
                FilledTonalButton(onClick = onDescription, modifier = Modifier.weight(1f).widthIn(min = actionWidth),
                    contentPadding = PaddingValues(16.dp), shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Filled.Edit, null, Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Column { Text(stringResource(R.string.record_description)); Text(stringResource(R.string.record_description_hint), style = MaterialTheme.typography.bodySmall) }
                }
            }
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onManual) { Text(stringResource(R.string.daily_manual)) }
                // The popup anchor must move with this button inside the scrolling diary.
                Box {
                    TextButton(onClick = { reuseExpanded = true }) { Text(stringResource(R.string.home_menu_reuse_meal)) }
                    SheetGlassDropdownMenu(expanded = reuseExpanded, onDismissRequest = { reuseExpanded = false }, menuWidth = 238.dp) {
                        listOf(FoodLogMethod.RECENT, FoodLogMethod.FREQUENT, FoodLogMethod.FAVORITES, FoodLogMethod.COPY_FROM_DAY).forEach { method ->
                            SheetGlassDropdownMenuItem(label = stringResource(method.titleRes), leadingIcon = method.icon) {
                                reuseExpanded = false
                                onReuse(method)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DailyRecommendationLink(onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Restaurant, null)
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.recommend_title), style = MaterialTheme.typography.titleSmall)
                Text(stringResource(R.string.recommend_subtitle), style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
        }
    }
}

@Composable
internal fun DailyWorkoutLink(container: AppContainer, date: LocalDate, onClick: () -> Unit) {
    val flow = remember(container, date) { container.workoutRepository.exercises(date) }
    val exercises by flow.collectAsState(initial = emptyList())
    val performed = exercises.sumOf { exercise -> exercise.sets.count { it.reps.isNotBlank() } }
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.FitnessCenter, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.daily_workout), style = MaterialTheme.typography.titleSmall)
                Text(if (exercises.isEmpty()) stringResource(R.string.daily_workout_empty)
                    else stringResource(R.string.daily_workout_count, exercises.size, performed),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
        }
    }
}
