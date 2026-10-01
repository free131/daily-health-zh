package com.apoorvdarshan.calorietracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apoorvdarshan.calorietracker.models.MealType

/** Display choices separately from the persisted MealType values. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MealTypeSelector(selected: MealType, onSelect: (MealType) -> Unit) {
    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK, MealType.OTHER).forEach { meal ->
            FilterChip(selected = meal == selected, onClick = { onSelect(meal) },
                label = { Text(stringResource(meal.displayNameRes)) })
        }
    }
}
