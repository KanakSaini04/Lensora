package com.kanak.lensora

import com.kanak.lensora.ui.graphics.AgslShaderManager
import com.kanak.lensora.ui.graphics.HorizonLevelCalculator
import com.kanak.lensora.ui.graphics.ShaderMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AgslShaderTest {

    @Test
    fun testFalseColorLuminanceMapping() {
        assertEquals("RED (Overexposed)", AgslShaderManager.mapLuminanceToFalseColor(1.0f))
        assertEquals("YELLOW (Highlights)", AgslShaderManager.mapLuminanceToFalseColor(0.90f))
        assertEquals("PINK (Skin Tone 55%)", AgslShaderManager.mapLuminanceToFalseColor(0.55f))
        assertEquals("GREEN (18% Mid-Gray)", AgslShaderManager.mapLuminanceToFalseColor(0.40f))
        assertEquals("BLUE (Shadows)", AgslShaderManager.mapLuminanceToFalseColor(0.10f))
        assertEquals("PURPLE (Under-exposed)", AgslShaderManager.mapLuminanceToFalseColor(0.02f))
    }

    @Test
    fun testHorizonLevelCalculator() {
        // Level within 0.5 degrees -> isLevel = true
        val levelState = HorizonLevelCalculator.calculateTiltState(pitch = 0.2f, roll = -0.3f, toleranceDeg = 0.5f)
        assertTrue(levelState.isLevel)
        assertEquals(0.2f, levelState.pitchDeg, 0.01f)
        assertEquals(-0.3f, levelState.rollDeg, 0.01f)

        // Tilted beyond 0.5 degrees -> isLevel = false
        val tiltedState = HorizonLevelCalculator.calculateTiltState(pitch = 2.5f, roll = -4.0f, toleranceDeg = 0.5f)
        assertFalse(tiltedState.isLevel)
    }

    @Test
    fun testShaderModeEnum() {
        val modes = ShaderMode.entries
        assertTrue(modes.contains(ShaderMode.FOCUS_PEAKING))
        assertTrue(modes.contains(ShaderMode.ZEBRA_STRIPES))
        assertTrue(modes.contains(ShaderMode.FALSE_COLOR))
        assertTrue(modes.contains(ShaderMode.ANAMORPHIC_FLARE))
    }
}
