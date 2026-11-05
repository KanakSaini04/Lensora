package com.kanak.lensora

import app.cash.turbine.test
import com.kanak.lensora.neural.VisionPipelineManager
import com.kanak.lensora.neural.YuvHistogramAnalyzer
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class YuvHistogramAnalyzerTest {

    @Test
    fun testHistogramCalculationAndClippingRatios() {
        val analyzer = YuvHistogramAnalyzer()

        // Test array: 10 pixels: 2 shadow (val 0), 2 highlight (val 255), 6 mid-tone (val 128)
        val sampleBytes = byteArrayOf(
            0.toByte(), 0.toByte(),
            255.toByte(), 255.toByte(),
            128.toByte(), 128.toByte(), 128.toByte(), 128.toByte(), 128.toByte(), 128.toByte()
        )

        val result = analyzer.computeHistogramFromByteArray(sampleBytes)

        assertEquals(2, result.bins[0])
        assertEquals(2, result.bins[255])
        assertEquals(6, result.bins[128])

        assertEquals(0.2f, result.shadowClippingRatio, 0.01f)
        assertEquals(0.2f, result.highlightClippingRatio, 0.01f)
    }

    @Test
    fun testVisionPipelineFlow() = runTest {
        val manager = VisionPipelineManager()

        manager.metricsState.test {
            val initial = awaitItem()
            assertFalse(initial.isHandheldShakeDetected)
            assertEquals(0.95f, initial.frameStabilityIndex, 0.01f)

            // High displacement (motion velocity shake)
            manager.processFrameDisplacement(dx = 150f, dy = 200f, deltaTimeMs = 33L)
            val updated = awaitItem()
            assertTrue(updated.isHandheldShakeDetected)
            assertTrue(updated.motionVelocityScore > 0.35f)
        }
    }
}
