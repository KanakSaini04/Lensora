package com.kanak.lensora.optics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class holding tactile dial configuration and position.
 */
data class DialValue(
    val label: String,
    val currentValue: Float,
    val minValue: Float,
    val maxValue: Float,
    val step: Float
)

/**
 * Manages tactile manual dial values with reactive [StateFlow] updates.
 */
class HapticDialState {
    private val _isoDial = MutableStateFlow(DialValue("ISO", 100f, 50f, 6400f, 50f))
    val isoDial: StateFlow<DialValue> = _isoDial.asStateFlow()

    fun setIso(value: Float) {
        val clamped = value.coerceIn(_isoDial.value.minValue, _isoDial.value.maxValue)
        _isoDial.value = _isoDial.value.copy(currentValue = clamped)
    }
}
