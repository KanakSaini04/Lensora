package com.kanak.lensora.optics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing live physical 200MP ISOCELL HP3 sensor telemetry state.
 */
data class SensorTelemetryState(
    val iso: Int = 100,
    val exposureTimeNs: Long = 1_000_000L,
    val focusDiopters: Float = 0.0f,
    val is200MpActive: Boolean = true
)

/**
 * High-performance state manager delivering non-blocking telemetry updates via [StateFlow].
 */
class SensorTelemetry {
    private val _telemetryState = MutableStateFlow(SensorTelemetryState())
    val telemetryState: StateFlow<SensorTelemetryState> = _telemetryState.asStateFlow()

    fun updateTelemetry(iso: Int, exposureNs: Long, diopters: Float) {
        _telemetryState.value = SensorTelemetryState(
            iso = iso,
            exposureTimeNs = exposureNs,
            focusDiopters = diopters,
            is200MpActive = true
        )
    }
}
