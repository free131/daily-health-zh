package com.apoorvdarshan.calorietracker.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class BodyMetricsSyncTest {
    @Test fun eachPermissionCombinationOnlyWritesAuthorizedMetrics() = runBlocking {
        for (weight in listOf(false, true)) for (fat in listOf(false, true)) {
            val writes = mutableListOf<String>()
            syncBodyMetricsIndependently({ weight }, { writes.add("weight") }, { fat }, { writes.add("fat") })
            assertEquals(listOfNotNull("weight".takeIf { weight }, "fat".takeIf { fat }), writes)
        }
    }
    @Test fun failedWeightWriteDoesNotBlockBodyFat() = runBlocking {
        var fatWritten = false
        syncBodyMetricsIndependently({ true }, { error("platform weight failure") }, { true }, { fatWritten = true })
        assertTrue(fatWritten)
    }
    @Test fun failedWeightPermissionQueryDoesNotBlockBodyFat() = runBlocking {
        var fatWritten = false
        syncBodyMetricsIndependently({ error("permission query failure") }, { fail("unauthorized weight write") }, { true }, { fatWritten = true })
        assertTrue(fatWritten)
    }
    @Test fun cancellationIsNotSwallowed() = runBlocking {
        var fatWritten = false
        try {
            syncBodyMetricsIndependently({ true }, { throw CancellationException() }, { true }, { fatWritten = true })
            fail("Expected cancellation")
        } catch (_: CancellationException) { assertFalse(fatWritten) }
    }
}
