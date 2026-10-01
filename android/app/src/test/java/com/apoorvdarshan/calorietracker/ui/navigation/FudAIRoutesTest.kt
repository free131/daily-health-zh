package com.apoorvdarshan.calorietracker.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FudAIRoutesTest {
    @Test
    fun settingsAndCoachAreSecondaryDestinations() {
        assertNull(FudAIRoutes.selectedBottomTab(FudAIRoutes.SETTINGS))
        assertNull(FudAIRoutes.selectedBottomTab(FudAIRoutes.COACH))
        assertEquals(FudAIRoutes.RECORDS, FudAIRoutes.selectedBottomTab(FudAIRoutes.RECORDS))
        assertNull(
            FudAIRoutes.selectedBottomTab(FudAIRoutes.QUICK_ACTIONS)
        )
        assertNull(
            FudAIRoutes.selectedBottomTab(FudAIRoutes.OPTIONAL_NUTRIENT_GOALS)
        )
        assertEquals(FudAIRoutes.HOME, FudAIRoutes.selectedBottomTab(FudAIRoutes.HOME))
        assertNull(FudAIRoutes.selectedBottomTab(FudAIRoutes.ONBOARDING))
        assertNull(FudAIRoutes.selectedBottomTab(null))
    }
}
