package com.apoorvdarshan.calorietracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private fun lightColors(themeColor: AppThemeColor) = lightColorScheme(
    primary = themeColor.start,
    onPrimary = AppColors.OnDark,
    secondary = themeColor.start,
    onSecondary = AppColors.OnDark,
    tertiary = themeColor.start,
    onTertiary = AppColors.OnDark,
    background = AppColors.AppBackgroundLight,
    onBackground = AppColors.OnLight,
    surface = AppColors.AppCardLight,
    onSurface = AppColors.OnLight,
    surfaceVariant = AppColors.AppCardLight,
    onSurfaceVariant = AppColors.MutedLight,
    outline = AppColors.DividerLight,
    primaryContainer = themeColor.start.copy(alpha = 0.12f),
    onPrimaryContainer = AppColors.OnLight,
    secondaryContainer = themeColor.start.copy(alpha = 0.10f),
    onSecondaryContainer = AppColors.OnLight,
    surfaceTint = themeColor.start
)

private fun darkColors(themeColor: AppThemeColor) = darkColorScheme(
    primary = if (themeColor == AppThemeColor.DAILY_GREEN) androidx.compose.ui.graphics.Color(0xFF74D5B3) else themeColor.start,
    onPrimary = if (themeColor == AppThemeColor.DAILY_GREEN) AppColors.AppBackgroundDark else AppColors.OnDark,
    secondary = themeColor.start,
    onSecondary = AppColors.OnDark,
    tertiary = themeColor.start,
    onTertiary = AppColors.OnDark,
    background = AppColors.AppBackgroundDark,
    onBackground = AppColors.OnDark,
    surface = AppColors.AppCardDark,
    onSurface = AppColors.OnDark,
    surfaceVariant = AppColors.AppCardDark,
    onSurfaceVariant = AppColors.MutedDark,
    outline = AppColors.DividerDark,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF294438),
    onPrimaryContainer = AppColors.OnDark,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF294438),
    onSecondaryContainer = AppColors.OnDark,
    surfaceTint = themeColor.start
)

@Composable
fun FudAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: AppThemeColor = AppThemeColor.DAILY_GREEN,
    content: @Composable () -> Unit
) {
    AppColors.setThemeColor(themeColor)
    val colorScheme = if (darkTheme) darkColors(themeColor) else lightColors(themeColor)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
