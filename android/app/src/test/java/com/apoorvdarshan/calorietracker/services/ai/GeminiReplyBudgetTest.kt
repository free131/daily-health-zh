package com.apoorvdarshan.calorietracker.services.ai

import com.apoorvdarshan.calorietracker.models.AIProvider
import kotlinx.serialization.json.*
import org.junit.Assert.assertEquals
import org.junit.Test

class GeminiReplyBudgetTest {
    @Test fun thinkingKeepsLargerCallerBudgetAndNormalModeKeepsExactBudget() {
        for (budget in listOf(3072, 16000)) for (thinking in listOf(false, true)) {
            val request = buildJsonObject { putJsonObject("generationConfig") { put("maxOutputTokens", budget) } }
            val result = ThinkingMode.apply(request, AIProvider.GEMINI, "gemini-3.5-flash-lite", thinking)
            assertEquals(if (thinking) maxOf(8192, budget) else budget,
                result["generationConfig"]!!.jsonObject["maxOutputTokens"]!!.jsonPrimitive.int)
        }
    }
}
