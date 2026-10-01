package com.apoorvdarshan.calorietracker.models
import kotlin.math.roundToInt
object BodyMetricInput {
    fun wheelTenths(value: Double, min: Int, max: Int): Int =
        (value.coerceIn(min.toDouble(), max.toDouble()) * 10).roundToInt().coerceIn(min * 10, max * 10)
}
