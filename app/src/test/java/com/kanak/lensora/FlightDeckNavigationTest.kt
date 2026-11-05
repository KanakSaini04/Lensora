package com.kanak.lensora

import com.kanak.lensora.optics.DialValue
import com.kanak.lensora.optics.HapticDialState
import com.kanak.lensora.ui.FlightDeckTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlightDeckNavigationTest {

    @Test
    fun testFlightDeckTabs() {
        val tabs = FlightDeckTab.entries
        assertEquals(4, tabs.size)
        assertEquals("RIG", FlightDeckTab.RIG.title)
        assertEquals("OPTICS", FlightDeckTab.OPTICS.title)
        assertEquals("NEURAL", FlightDeckTab.NEURAL.title)
        assertEquals("QUANTUM", FlightDeckTab.QUANTUM.title)
    }

    @Test
    fun testDialClampingAndStepping() {
        val isoDial = DialValue("ISO", 100f, 50f, 6400f, 50f)

        // Clamping below min -> 50
        assertEquals(50f, isoDial.clamp(10f), 0.01f)

        // Clamping above max -> 6400
        assertEquals(6400f, isoDial.clamp(10000f), 0.01f)

        // Stepping rounding: 123f with step 50 -> 100f
        assertEquals(100f, isoDial.clamp(123f), 0.01f)

        // Stepping rounding: 130f with step 50 -> 150f
        assertEquals(150f, isoDial.clamp(130f), 0.01f)
    }

    @Test
    fun testHapticDialStateTicks() {
        val state = HapticDialState()
        var hapticTickFired = false

        state.updateIso(400f) {
            hapticTickFired = true
        }

        assertTrue(hapticTickFired)
        assertEquals(400f, state.isoDial.value.currentValue, 0.01f)
    }
}
