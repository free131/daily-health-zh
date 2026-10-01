package com.apoorvdarshan.calorietracker.services

import okhttp3.Dns
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class ApiOnlyNetworkPolicyTest {
    @Test fun blocksNonAiBeforeDns() {
        val dnsCalls = AtomicInteger()
        val client = SecureHttpClient.builder().dns(Dns {
            dnsCalls.incrementAndGet()
            error("DNS must not be reached")
        }).build()
        for (url in listOf("https://backup.invalid/upload", "https://updates.invalid/latest", "https://images.invalid/food.jpg")) {
            val request = Request.Builder().url(url).post("{}".toRequestBody()).build()
            try { client.newCall(request).execute().close(); fail("Non-AI request escaped") }
            catch (error: IOException) { assertTrue(error.message.orEmpty().contains("AI API")) }
        }
        assertEquals(0, dnsCalls.get())
    }

    @Test fun rejectsTaggedGetBeforeDns() {
        val client = SecureHttpClient.builder().dns(Dns { error("DNS must not be reached") }).build()
        val request = Request.Builder().url("https://api.invalid/models")
            .tag(AiApiRequest::class.java, AiApiRequest).get().build()
        try { client.newCall(request).execute().close(); fail("GET must be rejected") }
        catch (error: IOException) { assertTrue(error.message.orEmpty().contains("AI API")) }
    }

    @Test fun permitsExplicitAiPostWithoutLeakingInternalMarker() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("ok"))
            val client = SecureHttpClient.builder().build()
            val request = Request.Builder().url(server.url("/v1/chat/completions"))
                .tag(AiApiRequest::class.java, AiApiRequest).post("{}".toRequestBody()).build()
            client.newCall(request).execute().use { assertEquals("ok", it.body!!.string()) }
            val received = server.takeRequest()
            assertEquals("POST", received.method)
            assertFalse(received.headers.toString().contains("AiApiRequest"))
            client.connectionPool.evictAll()
            client.dispatcher.executorService.shutdown()
        }
    }

    @Test fun doesNotFollowRedirectOrForwardCredentials() {
        MockWebServer().use { source ->
            MockWebServer().use { destination ->
                destination.enqueue(MockResponse().setBody("should not be reached"))
                source.enqueue(MockResponse().setResponseCode(307).setHeader("Location", destination.url("/capture")))
                val client = SecureHttpClient.builder().build()
                val request = Request.Builder().url(source.url("/v1/chat/completions"))
                    .tag(AiApiRequest::class.java, AiApiRequest).post("{}".toRequestBody())
                    .header("Authorization", "Bearer test-placeholder").build()
                client.newCall(request).execute().use { assertEquals(307, it.code) }
                assertEquals(1, source.requestCount)
                assertEquals(0, destination.requestCount)
                client.connectionPool.evictAll()
                client.dispatcher.executorService.shutdown()
            }
        }
    }
}
