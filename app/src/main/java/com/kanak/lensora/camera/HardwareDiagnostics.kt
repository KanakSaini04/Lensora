package com.kanak.lensora.camera

import kotlin.math.abs

/**
 * Diagnostic report summarizing camera pipeline health, 2160Hz touch latency, and thermal state.
 */
data class DiagnosticsReport(
    val isSessionBound: Boolean = true,
    val fps: Float = 120.0f,
    val touchLatencyMs: Float = 0.46f,
    val thermalThrottlingDetected: Boolean = false,
    val isHealthPassed: Boolean = true
)

/**
 * Hardware diagnostics and session binding validator for the 200MP ISOCELL HP3 camera pipeline.
 */
object HardwareDiagnostics {

    /**
     * Verifies camera provider binding and surface readiness.
     */
    fun verifySessionBinding(cameraProviderBound: Boolean, surfaceActive: Boolean): Boolean {
        return cameraProviderBound && surfaceActive
    }

    /**
     * Calculates touch-to-frame latency in milliseconds for 2160Hz touch sampling layer validation.
     */
    fun calculateTouchLatencyMs(touchTimestampNs: Long, frameTimestampNs: Long): Float {
        val diffNs = abs(frameTimestampNs - touchTimestampNs)
        return (diffNs / 1_000_000.0f).coerceAtLeast(0.01f)
    }

    /**
     * Runs full pipeline health check and generates a [DiagnosticsReport].
     */
    fun generateReport(
        isBound: Boolean,
        isSurfaceActive: Boolean,
        fps: Float,
        touchTimestampNs: Long,
        frameTimestampNs: Long,
        isThermalThrottled: Boolean
    ): DiagnosticsReport {
        val sessionOk = verifySessionBinding(isBound, isSurfaceActive)
        val latency = calculateTouchLatencyMs(touchTimestampNs, frameTimestampNs)
        val healthPassed = sessionOk && fps >= 30.0f && !isThermalThrottled

        return DiagnosticsReport(
            isSessionBound = sessionOk,
            fps = fps,
            touchLatencyMs = latency,
            thermalThrottlingDetected = isThermalThrottled,
            isHealthPassed = healthPassed
        )
    }
}
