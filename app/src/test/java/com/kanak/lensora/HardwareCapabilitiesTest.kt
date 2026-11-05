package com.kanak.lensora

import com.kanak.lensora.camera.LensCapabilities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HardwareCapabilitiesTest {

    @Test
    fun testFixedFocusSafetyCheckForAuxiliaryLenses() {
        val fixedFocusLens = LensCapabilities(
            cameraId = "1",
            minFocusDistanceDiopters = 0.0f,
            isFixedFocus = true,
            supportsManualFocus = false
        )
        assertFalse(fixedFocusLens.canOverrideFocusManually())

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
            exposureTimeMaxNs = 2_000_000_000L
        )
        assertEquals("1/8000s - 2s", capabilities.formattedExposureTimeRange())
    }
}
