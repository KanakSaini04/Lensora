package com.kanak.lensora.ui.graphics

import android.graphics.RuntimeShader
import android.os.Build

enum class ShaderMode {
    NONE,
    FOCUS_PEAKING,
    ZEBRA_STRIPES,
    FALSE_COLOR,
    ANAMORPHIC_FLARE
}

/**
 * Manager component instantiating and configuring AGSL [RuntimeShader] instances on Android 13+ (API 33+).
 */
object AgslShaderManager {

    fun isAgslSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun createRuntimeShader(
        mode: ShaderMode,
        width: Float,
        height: Float,
        timeSec: Float = 0f
    ): RuntimeShader? {
        if (!isAgslSupported() || mode == ShaderMode.NONE) return null

        val shaderString = when (mode) {
            ShaderMode.FOCUS_PEAKING -> ViewfinderShaders.FOCUS_PEAKING_SHADER
            ShaderMode.ZEBRA_STRIPES -> ViewfinderShaders.ZEBRA_STRIPES_SHADER
            ShaderMode.FALSE_COLOR -> ViewfinderShaders.FALSE_COLOR_SHADER
            ShaderMode.ANAMORPHIC_FLARE -> ViewfinderShaders.ANAMORPHIC_FLARE_SHADER
            ShaderMode.NONE -> return null
        }

        return try {
            val shader = RuntimeShader(shaderString)
            if (mode == ShaderMode.FOCUS_PEAKING) {
                shader.setFloatUniform("resolution", width, height)
                shader.setFloatUniform("threshold", 0.18f)
            } else if (mode == ShaderMode.ZEBRA_STRIPES) {
                shader.setFloatUniform("resolution", width, height)
                shader.setFloatUniform("time", timeSec)
            } else if (mode == ShaderMode.ANAMORPHIC_FLARE) {
                shader.setFloatUniform("resolution", width, height)
            }
            shader
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Pure testable helper validating false color luminance zone mapping logic.
     */
    fun mapLuminanceToFalseColor(lum: Float): String {
        return when {
            lum > 0.99f -> "RED (Overexposed)"
            lum > 0.85f -> "YELLOW (Highlights)"
            lum in 0.52f..0.58f -> "PINK (Skin Tone 55%)"
            lum in 0.38f..0.44f -> "GREEN (18% Mid-Gray)"
            lum in 0.05f..0.15f -> "BLUE (Shadows)"
            lum <= 0.05f -> "PURPLE (Under-exposed)"
            else -> "GRAY (Midtones)"
        }
    }
}
