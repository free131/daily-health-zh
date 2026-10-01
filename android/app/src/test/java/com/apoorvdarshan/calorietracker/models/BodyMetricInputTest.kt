package com.apoorvdarshan.calorietracker.models
import org.junit.Assert.*
import org.junit.Test
class BodyMetricInputTest {
    @Test fun everyBodyFatTenthSurvivesWheelInitialization() {
        for (tenth in 30..600) assertEquals(tenth, BodyMetricInput.wheelTenths(tenth / 10.0, 3, 60))
    }
    @Test fun everyWeightTenthSurvivesWheelInitialization() {
        for (tenth in 10..5000) assertEquals(tenth, BodyMetricInput.wheelTenths(tenth / 10.0, 1, 500))
    }
    @Test fun wheelBoundariesClampWithoutOverflow() {
        assertEquals(600, BodyMetricInput.wheelTenths(60.9, 3, 60))
        assertEquals(30, BodyMetricInput.wheelTenths(2.9, 3, 60))
    }
}
