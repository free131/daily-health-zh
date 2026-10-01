package com.apoorvdarshan.calorietracker.services

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/** Barcode networking was removed. Even a supplied unrestricted client must not be used. */
class OpenFoodFactsServiceTest {
    @Test fun lookupAndProductImageNeverUseTheNetwork() = runBlocking {
        val calls = AtomicInteger()
        val client = OkHttpClient.Builder().addInterceptor {
            calls.incrementAndGet()
            error("Removed transport must never execute")
        }.build()
        for (withImage in listOf(false, true)) {
            try {
                if (withImage) OpenFoodFactsService.lookupWithImage("3017620422003", client)
                else OpenFoodFactsService.lookup("3017620422003", client)
                fail("Lookup should be unavailable")
            } catch (error: OpenFoodFactsService.LookupException) {
                assertEquals(OpenFoodFactsService.LookupFailure.NETWORK, error.failure)
            }
        }
        assertEquals(0, calls.get())
    }

    @Test fun barcodeValidationStillSupportsExistingLocalData() {
        assertEquals("0012345678905", OpenFoodFactsService.normalizedBarcode(" 0012345678905 "))
        assertNull(OpenFoodFactsService.normalizedBarcode(""))
        assertNull(OpenFoodFactsService.normalizedBarcode("123/456"))
        assertNull(OpenFoodFactsService.normalizedBarcode("1".repeat(25)))
    }
}
