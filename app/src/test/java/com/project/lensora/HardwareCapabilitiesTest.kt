package com.project.lensora

import com.project.lensora.camera.LensCapabilities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HardwareCapabilitiesTest {

    @Test
    fun testFixedFocusSafetyCheckForAuxiliaryLenses() {
        // Fixed-focus auxiliary lens (minFocusDistanceDiopters = 0.0f)
        val fixedFocusLens = LensCapabilities(
            cameraId = "1",
            minFocusDistanceDiopters = 0.0f,
            isFixedFocus = true,
            supportsManualFocus = false
        )
        assertFalse(fixedFocusLens.canOverrideFocusManually())

        // Variable-focus main 200MP lens (minFocusDistanceDiopters = 10.0f -> 10cm macro focus)
        val mainSensorLens = LensCapabilities(
            cameraId = "0",
            minFocusDistanceDiopters = 10.0f,
            isFixedFocus = false,
            supportsManualFocus = true
        )
        assertTrue(mainSensorLens.canOverrideFocusManually())
    }

    @Test
    fun testFormattedExposureTimeRange() {
        val capabilities = LensCapabilities(
            exposureTimeMinNs = 125_000L,
            exposureTimeMaxNs = 2_000_000_000L // 1/8000s to 2s
        )
        assertEquals("1/8000s - 2s", capabilities.formattedExposureTimeRange())
    }
}
