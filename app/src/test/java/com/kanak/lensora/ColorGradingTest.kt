package com.kanak.lensora

import com.kanak.lensora.data.ColorGradingEngine
import com.kanak.lensora.data.ColorGradingParameters
import com.kanak.lensora.data.PresetTemplateManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ColorGradingTest {

    @Test
    fun testColorMatrixGenerationAndIntensityBlending() {
        val params = ColorGradingParameters(
            brightness = 0.2f,
            contrast = 1.3f,
            saturation = 1.2f,
            intensity = 1.0f
        )

        val matrix = ColorGradingEngine.generate4x5Matrix(params)
        assertEquals(20, matrix.size)

        // At intensity = 0.0, the matrix should blend 100% to identity
        val zeroIntensityParams = params.copy(intensity = 0.0f)
        val identityMatrix = ColorGradingEngine.generate4x5Matrix(zeroIntensityParams)

        assertEquals(1.0f, identityMatrix[0], 0.001f) // R -> R
        assertEquals(0.0f, identityMatrix[1], 0.001f) // G -> R
        assertEquals(0.0f, identityMatrix[4], 0.001f) // Offset
        assertEquals(1.0f, identityMatrix[6], 0.001f) // G -> G
        assertEquals(1.0f, identityMatrix[12], 0.001f) // B -> B
        assertEquals(1.0f, identityMatrix[18], 0.001f) // A -> A
    }

    @Test
    fun testBuiltInPresetsLoading() {
        val greenLake = PresetTemplateManager.getPresetById("green_lake")
        assertNotNull(greenLake)
        assertEquals("Green Lake", greenLake!!.name)
        assertEquals(1.2f, greenLake.parameters.contrast, 0.01f)

        val maldives = PresetTemplateManager.getPresetById("maldives")
        assertNotNull(maldives)
        assertEquals("Maldives", maldives!!.name)
        assertEquals(-0.20f, maldives.parameters.temperature, 0.01f)
    }

    @Test
    fun testJsonTemplateSerializationRoundtrip() {
        val originalPreset = PresetTemplateManager.getPresetById("green_lake")!!
        val jsonString = PresetTemplateManager.toJson(originalPreset)

        assertNotNull(jsonString)
        val parsedPreset = PresetTemplateManager.fromJson(jsonString)

        assertNotNull(parsedPreset)
        assertEquals(originalPreset.id, parsedPreset!!.id)
        assertEquals(originalPreset.name, parsedPreset.name)
        assertEquals(originalPreset.parameters.contrast, parsedPreset.parameters.contrast, 0.01f)
        assertEquals(originalPreset.parameters.temperature, parsedPreset.parameters.temperature, 0.01f)
    }
}
