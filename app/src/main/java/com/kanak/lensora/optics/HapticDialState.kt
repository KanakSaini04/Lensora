package com.kanak.lensora.optics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round

/**
 * Data class holding tactile dial configuration and position.
 */
data class DialValue(
    val label: String,
    val currentValue: Float,
    val minValue: Float,
    val maxValue: Float,
    val step: Float
) {
    fun clamp(value: Float): Float {
        val clamped = value.coerceIn(minValue, maxValue)
        val stepsCount = round((clamped - minValue) / step)
        return minValue + (stepsCount * step)
    }
}

/**
 * Manages tactile manual dial values with reactive [StateFlow] updates and 2160Hz haptic feedback ticks.
 */
class HapticDialState {

    private val _isoDial = MutableStateFlow(DialValue("ISO", 100f, 50f, 6400f, 50f))
    val isoDial: StateFlow<DialValue> = _isoDial.asStateFlow()

    private val _shutterDial = MutableStateFlow(DialValue("Shutter Speed", 125f, 1f, 8000f, 10f)) // e.g. 1/125s
    val shutterDial: StateFlow<DialValue> = _shutterDial.asStateFlow()

    private val _kelvinWbDial = MutableStateFlow(DialValue("Kelvin WB", 5500f, 2000f, 10000f, 100f))
    val kelvinWbDial: StateFlow<DialValue> = _kelvinWbDial.asStateFlow()

    private val _focusDiopterDial = MutableStateFlow(DialValue("Focus Diopter", 0.0f, 0.0f, 10.0f, 0.1f))
    val focusDiopterDial: StateFlow<DialValue> = _focusDiopterDial.asStateFlow()

    fun updateIso(newValue: Float, onHapticTick: () -> Unit = {}): Float {
        val current = _isoDial.value
        val clamped = current.clamp(newValue)
        if (clamped != current.currentValue) {
            _isoDial.value = current.copy(currentValue = clamped)
            onHapticTick()
        }
        return clamped
    }

    fun updateShutter(newValue: Float, onHapticTick: () -> Unit = {}): Float {
        val current = _shutterDial.value
        val clamped = current.clamp(newValue)
        if (clamped != current.currentValue) {
            _shutterDial.value = current.copy(currentValue = clamped)
            onHapticTick()
        }
        return clamped
    }

    fun updateKelvinWb(newValue: Float, onHapticTick: () -> Unit = {}): Float {
        val current = _kelvinWbDial.value
        val clamped = current.clamp(newValue)
        if (clamped != current.currentValue) {
            _kelvinWbDial.value = current.copy(currentValue = clamped)
            onHapticTick()
        }
        return clamped
    }

    fun updateFocusDiopter(newValue: Float, onHapticTick: () -> Unit = {}): Float {
        val current = _focusDiopterDial.value
        val clamped = current.clamp(newValue)
        if (clamped != current.currentValue) {
            _focusDiopterDial.value = current.copy(currentValue = clamped)
            onHapticTick()
        }
        return clamped
    }
}
