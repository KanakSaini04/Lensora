package com.kanak.lensora

import com.kanak.lensora.neural.HistogramResult
import com.kanak.lensora.optics.DialValue
import com.kanak.lensora.ui.graphics.GyroSpecularSheen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FlightDeckUiEnhancementsTest {

    @Test
    fun testGyroSpecularSheenCalculation() {
        val brush = GyroSpecularSheen.calculateSpecularGradient(pitchDeg = 15f, rollDeg = -20f)
        assertNotNull(brush)
    }

    @Test
    fun testElasticScrubberBoundaryClamping() {
        val isoDial = DialValue("ISO", 100f, 50f, 6400f, 50f)

        // Lower boundary elastic clamp
        assertEquals(50f, isoDial.clamp(-50f), 0.01f)

        // Upper boundary elastic clamp
        assertEquals(6400f, isoDial.clamp(10000f), 0.01f)
    }

    @Test
    fun testMicroWaveformHistogramResult() {
        val bins = IntArray(256) { if (it % 2 == 0) 10 else 0 }
        val histogramResult = HistogramResult(
            bins = bins,
            shadowClippingRatio = 0.05f,
            highlightClippingRatio = 0.01f,
            meanLuminance = 120.0f
        )

        assertEquals(256, histogramResult.bins.size)
        assertEquals(10, histogramResult.bins[0])
        assertEquals(0, histogramResult.bins[1])
        assertEquals(0.05f, histogramResult.shadowClippingRatio, 0.001f)
    }
}
