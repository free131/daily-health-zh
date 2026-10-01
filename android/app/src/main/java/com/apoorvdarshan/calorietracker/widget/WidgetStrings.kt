package com.apoorvdarshan.calorietracker.widget

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import com.apoorvdarshan.calorietracker.R
import com.apoorvdarshan.calorietracker.models.HomeTopNutrient
import com.apoorvdarshan.calorietracker.models.WidgetNutrient

@Composable
internal fun widgetText(@StringRes id: Int, vararg args: Any): String {
    val context = LocalContext.current
    val locales = AppCompatDelegate.getApplicationLocales()
    val localized = if (locales.isEmpty) context else context.createConfigurationContext(
        Configuration(context.resources.configuration).apply { setLocale(locales[0]) }
    )
    return localized.getString(id, *args)
}

@Composable
internal fun widgetNutrientLabel(nutrient: WidgetNutrient): String {
    if (nutrient.id == "water") return widgetText(R.string.widget_water_tracking)
    val known = HomeTopNutrient.entries.firstOrNull { it.storageKey == nutrient.id }
    return known?.let { widgetText(it.displayNameRes) } ?: nutrient.label
}
