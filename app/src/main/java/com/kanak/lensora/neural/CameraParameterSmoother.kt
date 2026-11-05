package com.kanak.lensora.neural

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

/**
 * Exponential Moving Average (EMA) parameter smoother featuring a strict 4% deadband filter.
 * Completely eliminates viewfinder flickering during auto-exposure and white-balance transitions.
 */
class CameraParameterSmoother(
    val alpha: Float = 0.15f,
    val deadbandPercent: Float = 0.04f
) {
    private val _smoothedIso = MutableStateFlow(100f)
    val smoothedIso: StateFlow<Float> = _smoothedIso.asStateFlow()

    private val _smoothedExposureNs = MutableStateFlow(1_000_000f)
    val smoothedExposureNs: StateFlow<Float> = _smoothedExposureNs.asStateFlow()

    /**
     * Applies EMA damped smoothing with deadband filtering to a parameter value.
     */
    fun smoothValue(targetValue: Float, currentValue: Float): Float {
        if (currentValue <= 0f) return targetValue
        val diffRatio = abs(targetValue - currentValue) / currentValue

        // Deadband filter: changes <= 4% are suppressed to prevent viewfinder flickering
        if (diffRatio <= deadbandPercent) {
            return currentValue
        }

        return (alpha * targetValue) + ((1f - alpha) * currentValue)
    }

    /**
     * Updates and emits smoothed ISO state.
     */
    fun updateIso(targetIso: Float): Float {
        val current = _smoothedIso.value
        val smoothed = smoothValue(targetIso, current)
        _smoothedIso.value = smoothed
        return smoothed
    }

    /**
     * Updates and emits smoothed Exposure Time state.
     */
    fun updateExposureNs(targetExposureNs: Float): Float {
        val current = _smoothedExposureNs.value
        val smoothed = smoothValue(targetExposureNs, current)
        _smoothedExposureNs.value = smoothed
        return smoothed
    }
}
