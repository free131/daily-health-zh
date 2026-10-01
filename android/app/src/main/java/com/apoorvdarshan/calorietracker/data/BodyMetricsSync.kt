package com.apoorvdarshan.calorietracker.data

import kotlinx.coroutines.CancellationException

/** A denied permission or failed weight write must not suppress an authorized body-fat write. */
internal suspend fun syncBodyMetricsIndependently(
    canWriteWeight: suspend () -> Boolean,
    writeWeight: suspend () -> Unit,
    canWriteBodyFat: suspend () -> Boolean,
    writeBodyFat: suspend () -> Unit
) {
    suspend fun attempt(permission: suspend () -> Boolean, write: suspend () -> Unit) {
        try { if (permission()) write() }
        catch (e: CancellationException) { throw e }
        catch (_: Exception) { /* The platform writer owns its retry queue; local data is already saved. */ }
    }
    attempt(canWriteWeight, writeWeight)
    attempt(canWriteBodyFat, writeBodyFat)
}
