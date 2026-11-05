package com.kanak.lensora.neural

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.hypot

/**
 * Data model for frame stability and motion velocity metrics derived from MediaPipe / optical flow analysis.
 */
data class VisionMetricsState(
    val motionVelocityScore: Float = 0.05f,
    val frameStabilityIndex: Float = 0.95f,
    val isHandheldShakeDetected: Boolean = false
)

/**
 * Manager handling real-time MediaPipe Tasks Vision and frame stability metrics via Kotlin Flows.
 */
class VisionPipelineManager {

    private val _metricsState = MutableStateFlow(VisionMetricsState())
    val metricsState: StateFlow<VisionMetricsState> = _metricsState.asStateFlow()

    /**
     * Updates frame stability and motion velocity scores from inter-frame shift displacement.
     */
    fun processFrameDisplacement(dx: Float, dy: Float, deltaTimeMs: Long) {
        val distance = hypot(dx, dy)
        val timeSec = if (deltaTimeMs > 0) deltaTimeMs / 1000f else 0.033f
        val velocity = (distance / (timeSec * 1000f)).coerceIn(0f, 1f)
        val stability = (1.0f - velocity).coerceIn(0f, 1f)
        val isShake = velocity > 0.35f

        _metricsState.value = VisionMetricsState(
            motionVelocityScore = velocity,
            frameStabilityIndex = stability,
            isHandheldShakeDetected = isShake
        )
    }
}
