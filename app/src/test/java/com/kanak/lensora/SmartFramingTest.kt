package com.kanak.lensora

import com.kanak.lensora.neural.NeuralFramingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartFramingTest {

    @Test
    fun testRuleOfThirdsBalancedFraming() {
        val engine = NeuralFramingEngine()

        // Image dimensions: 1920 x 1080
        // Center: (960, 360) -> Perfectly at idealX (960) and idealY (360)
        val balanced = engine.calculateRuleOfThirds(
            centerX = 960f,
            centerY = 360f,
            width = 1920f,
            height = 1080f
        )

        assertTrue(balanced.isBalanced)
        assertEquals("Framing Balanced ✓", balanced.suggestionText)
        assertEquals(0.0f, balanced.targetOffsetX, 0.01f)
        assertEquals(0.0f, balanced.targetOffsetY, 0.01f)
    }

    @Test
    fun testRuleOfThirdsUnbalancedPanLeft() {
        val engine = NeuralFramingEngine()

        // Subject too far right -> CenterX = 1400 (> 960)
        val unbalanced = engine.calculateRuleOfThirds(
            centerX = 1400f,
            centerY = 360f,
            width = 1920f,
            height = 1080f
        )

        assertFalse(unbalanced.isBalanced)
        assertEquals("Pan Left", unbalanced.suggestionText)
    }

    @Test
    fun testRuleOfThirdsUnbalancedTiltUp() {
        val engine = NeuralFramingEngine()

        // Subject too far down -> CenterY = 800 (> 360)
        val unbalanced = engine.calculateRuleOfThirds(
            centerX = 960f,
            centerY = 800f,
            width = 1920f,
            height = 1080f
        )

        assertFalse(unbalanced.isBalanced)
        assertEquals("Tilt Up", unbalanced.suggestionText)
    }
}
