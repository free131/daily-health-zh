package com.apoorvdarshan.calorietracker.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apoorvdarshan.calorietracker.R

@Composable
internal fun DailyDestinationHeader(title: String, onSettings: (() -> Unit)? = null,
    onCoach: (() -> Unit)? = null, onBack: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.onboarding_back))
        }
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
        if (onCoach != null) TextButton(onClick = onCoach) { Text(stringResource(R.string.daily_ask_coach)) }
        if (onSettings != null) IconButton(onClick = onSettings) {
            Icon(Icons.Filled.Settings, stringResource(R.string.nav_settings))
        }
    }
}
