package com.kanak.lensora.ui.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Encapsulates dual-axis gyroscope pitch and roll tilt angles.
 */
data class GyroTiltState(
    val pitchDeg: Float = 0.0f,
    val rollDeg: Float = 0.0f,
    val isLevel: Boolean = true
)

object HorizonLevelCalculator {
    fun calculateTiltState(pitch: Float, roll: Float, toleranceDeg: Float = 0.5f): GyroTiltState {
        val level = abs(pitch) <= toleranceDeg && abs(roll) <= toleranceDeg
        return GyroTiltState(
            pitchDeg = pitch,
            rollDeg = roll,
            isLevel = level
        )
    }
}

/**
 * 120Hz Dual-Axis Gyroscope Horizon Level HUD Composable.
 * Uses Compose [graphicsLayer] deferral (`rotationZ`) to guarantee zero recomposition frame drops.
 */
@Composable
fun HorizonLevelOverlay(
    tiltState: GyroTiltState,
    modifier: Modifier = Modifier
) {
    val levelColor = if (tiltState.isLevel) Color(0xFF00E676) else Color(0xFF00E5FF)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = -tiltState.rollDeg
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val lineLength = 120.dp.toPx()

            // Left horizon guide line
            drawLine(
                color = levelColor,
                start = Offset(cx - lineLength, cy),
                end = Offset(cx - 30.dp.toPx(), cy),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Right horizon guide line
            drawLine(
                color = levelColor,
                start = Offset(cx + 30.dp.toPx(), cy),
                end = Offset(cx + lineLength, cy),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center leveling reticle circle
            drawCircle(
                color = levelColor,
                radius = 6.dp.toPx(),
                center = Offset(cx, cy + (tiltState.pitchDeg * 2.dp.toPx()))
            )
        }
    }
}
