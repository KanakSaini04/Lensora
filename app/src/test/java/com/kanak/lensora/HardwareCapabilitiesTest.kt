package com.kanak.lensora

import com.kanak.lensora.camera.LensCapabilities
import com.kanak.lensora.camera.PixelBinningMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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

    @Test
    fun testPixelBinningModes() {
        assertEquals(2.24f, PixelBinningMode.NIGHT_12_5MP.pixelSizeMicrons, 0.01f)
        assertEquals(1.12f, PixelBinningMode.PORTRAIT_50MP.pixelSizeMicrons, 0.01f)
        assertEquals(0.56f, PixelBinningMode.ULTRA_HD_200MP.pixelSizeMicrons, 0.01f)
    }

    @Test
    fun testInSensorCropRegionCalculation() {
        val capabilities = LensCapabilities(
            cameraId = "0",
            hasOis = true
        )

        // 2x In-Sensor Zoom on 16384x12288 array
        val crop2x = capabilities.calculateInSensorCropRegion(2.0f, 16384, 12288)
        assertNotNull(crop2x)
        assertEquals(8192, crop2x.width)
        assertEquals(6144, crop2x.height)
        assertEquals(4096, crop2x.left)
        assertEquals(3072, crop2x.top)
    }
}
