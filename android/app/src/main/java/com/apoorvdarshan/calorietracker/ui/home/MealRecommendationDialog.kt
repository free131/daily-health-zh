package com.apoorvdarshan.calorietracker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.apoorvdarshan.calorietracker.R
import com.apoorvdarshan.calorietracker.models.MealType

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MealRecommendationDialog(vm: MealRecommendationViewModel, onDismiss: () -> Unit) {
    val ui by vm.ui.collectAsState()
    var wholeDay by rememberSaveable { mutableStateOf(true) }
    var meal by rememberSaveable { mutableStateOf(MealType.currentMeal.let { if (it == MealType.OTHER) MealType.DINNER else it }) }
    var preference by rememberSaveable { mutableStateOf("") }
    fun close() { vm.cancel(); onDismiss() }
    Dialog(onDismissRequest = ::close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.padding(16.dp).widthIn(max = 680.dp).fillMaxWidth().fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.recommend_title), Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = ::close) { Icon(Icons.Default.Close, stringResource(R.string.recommend_close)) }
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.recommend_intro), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = wholeDay, onClick = { wholeDay = true }, enabled = !ui.loading,
                            label = { Text(stringResource(R.string.recommend_day)) })
                        FilterChip(selected = !wholeDay, onClick = { wholeDay = false }, enabled = !ui.loading,
                            label = { Text(stringResource(R.string.recommend_meal)) })
                    }
                    if (!wholeDay) FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK).forEach { value ->
                            FilterChip(selected = meal == value, onClick = { meal = value }, enabled = !ui.loading,
                                label = { Text(stringResource(value.displayNameRes)) })
                        }
                    }
                    OutlinedTextField(value = preference, onValueChange = { preference = it.take(500) }, enabled = !ui.loading,
                        modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4,
                        label = { Text(stringResource(R.string.recommend_preferences)) },
                        placeholder = { Text(stringResource(R.string.recommend_preferences_hint)) })
                    Button(onClick = { vm.generate(wholeDay, meal, preference) }, enabled = !ui.loading,
                        modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(if (ui.result == null) R.string.recommend_generate else R.string.recommend_regenerate))
                    }
                    if (ui.loading) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                            Text(stringResource(R.string.recommend_loading), Modifier.weight(1f))
                            TextButton(onClick = vm::cancel) { Text(stringResource(R.string.recommend_cancel)) }
                        }
                    }
                    ui.errorRes?.let {
                        Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                        Text(stringResource(R.string.recommend_retry_hint), style = MaterialTheme.typography.bodySmall)
                    }
                    ui.result?.let { result ->
                        HorizontalDivider()
                        Text("${ui.generatedFor} · ${ui.generatedAt}", style = MaterialTheme.typography.labelLarge)
                        Text(result.summary, style = MaterialTheme.typography.bodyMedium)
                        result.plans.forEachIndexed { index, plan ->
                            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("${index + 1}. ${plan.title}", style = MaterialTheme.typography.titleMedium)
                                    Text(plan.reason, style = MaterialTheme.typography.bodyMedium)
                                    plan.meals.forEach { food ->
                                        Text(food.name, fontWeight = FontWeight.SemiBold)
                                        Text(food.foods, style = MaterialTheme.typography.bodyMedium)
                                        Text(stringResource(R.string.recommend_calories, food.calories),
                                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                    HorizontalDivider()
                                    Text(stringResource(R.string.recommend_total, plan.calories), style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                        Text(stringResource(R.string.recommend_estimate), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
