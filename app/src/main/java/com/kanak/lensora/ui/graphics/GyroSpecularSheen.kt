package com.kanak.lensora.ui.graphics

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Calculates a brushed-titanium specular highlight linear gradient based on device gyroscope tilt.
 */
object GyroSpecularSheen {

    fun calculateSpecularGradient(pitchDeg: Float, rollDeg: Float): Brush {
        val shiftX = (rollDeg / 45f).coerceIn(-1.0f, 1.0f)
        val shiftY = (pitchDeg / 45f).coerceIn(-1.0f, 1.0f)

        val highlightAlpha = 0.25f
        val baseAlpha = 0.05f

        return Brush.linearGradient(
            0.0f + shiftX to Color.White.copy(alpha = baseAlpha),
            0.5f + (shiftX * 0.2f) to Color.White.copy(alpha = highlightAlpha),
            1.0f + shiftY to Color.White.copy(alpha = baseAlpha)
        )
    }
}
