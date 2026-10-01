package com.apoorvdarshan.calorietracker.services.ai

import com.apoorvdarshan.calorietracker.models.AIProvider
import org.junit.Assert.*
import org.junit.Test

class DeepSeekPresetTest {
    @Test fun presetIsVisibleForVisionAndText() {
        assertEquals(AIProvider.DEEPSEEK, AIProvider.remoteVisionProviders.first())
        assertEquals(AIProvider.DEEPSEEK, AIProvider.remoteTextProviders.first())
        assertEquals("https://api.deepseek.com", AIProvider.DEEPSEEK.baseUrl)
        assertEquals(AIProvider.ApiFormat.OPENAI_COMPATIBLE, AIProvider.DEEPSEEK.apiFormat)
        assertTrue(AIProvider.DEEPSEEK.requiresApiKey)
        assertFalse(AIProvider.DEEPSEEK.requiresCustomEndpoint)
    }
    @Test fun onlyFlashIsOfferedForImages() {
        assertEquals(listOf("deepseek-flash"), AIProvider.DEEPSEEK.models)
        assertEquals("deepseek-flash", AIProvider.DEEPSEEK.defaultModel)
        assertEquals(listOf("deepseek-flash", "deepseek-v4-pro"), AIProvider.DEEPSEEK.textModels)
        assertEquals("deepseek-flash", AIProvider.DEEPSEEK.supportedModelOrDefault("deepseek-v4-pro"))
        assertEquals("deepseek-v4-pro", AIProvider.DEEPSEEK.supportedTextModelOrDefault("deepseek-v4-pro"))
    }
    @Test fun presetUsesCompatibleTokenField() {
        assertEquals("max_tokens", OpenAICompatibleClient.tokenLimitParameter(AIProvider.DEEPSEEK, "deepseek-flash"))
    }
}
