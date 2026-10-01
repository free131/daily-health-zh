package com.apoorvdarshan.calorietracker.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apoorvdarshan.calorietracker.R
import com.apoorvdarshan.calorietracker.models.WorkoutTabMode

data class BottomTab(val route: String, val icon: ImageVector, @get:StringRes val labelRes: Int)
val BottomTabs = listOf(
    BottomTab(FudAIRoutes.HOME, Icons.Filled.Home, R.string.daily_nav_today),
    BottomTab(FudAIRoutes.RECORDS, Icons.Filled.EditNote, R.string.daily_nav_records),
    BottomTab(FudAIRoutes.WORKOUTS, Icons.Filled.FitnessCenter, R.string.nav_workouts),
    BottomTab(FudAIRoutes.PROGRESS, Icons.Filled.BarChart, R.string.daily_nav_trends)
)
val BottomNavScrollPadding = 112.dp
val BottomNavDockedControlPadding = 82.dp

@Composable
fun FudAIBottomNavBar(
    currentRoute: String?,
    showAboutBadge: Boolean = false,
    workoutMode: WorkoutTabMode = WorkoutTabMode.Default,
    onTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier, containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp) {
        BottomTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onTap(tab.route) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(stringResource(tab.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
