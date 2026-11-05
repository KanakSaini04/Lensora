package com.kanak.lensora

import com.kanak.lensora.camera.CameraSurfaceAutoHealer
import com.kanak.lensora.camera.HardwareDiagnostics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosticsTest {

    @Test
    fun testSessionBindingAndTouchLatency() {
        val isBound = HardwareDiagnostics.verifySessionBinding(cameraProviderBound = true, surfaceActive = true)
        assertTrue(isBound)

        val unbound = HardwareDiagnostics.verifySessionBinding(cameraProviderBound = true, surfaceActive = false)
        assertFalse(unbound)

        // Touch latency calculation (e.g. 1ms = 1_000_000 ns)
        val latencyMs = HardwareDiagnostics.calculateTouchLatencyMs(
            touchTimestampNs = 1_000_000_000L,
            frameTimestampNs = 1_000_460_000L // 0.46ms
        )
        assertEquals(0.46f, latencyMs, 0.01f)
    }

    @Test
    fun testDiagnosticsReportGeneration() {
        val healthyReport = HardwareDiagnostics.generateReport(
            isBound = true,
            isSurfaceActive = true,
            fps = 120f,
            touchTimestampNs = 1_000_000_000L,
            frameTimestampNs = 1_000_460_000L,
            isThermalThrottled = false
        )
        assertTrue(healthyReport.isSessionBound)
        assertTrue(healthyReport.isHealthPassed)
        assertEquals(120f, healthyReport.fps, 0.01f)

        val throttledReport = HardwareDiagnostics.generateReport(
            isBound = true,
            isSurfaceActive = true,
            fps = 15f,
            touchTimestampNs = 1_000_000_000L,
            frameTimestampNs = 1_000_460_000L,
            isThermalThrottled = true
        )
        assertFalse(throttledReport.isHealthPassed)
        assertTrue(throttledReport.thermalThrottlingDetected)
    }

    @Test
    fun testCameraSurfaceAutoHealerRecovery() {
        val autoHealer = CameraSurfaceAutoHealer()

        // Successful re-binding recovery
        val eventSuccess = autoHealer.handlePipelineStall("Thermal Throttling Stall") {
            true // Rebind successful
        }
        assertTrue(eventSuccess.recoverySuccessful)
        assertEquals("Thermal Throttling Stall", eventSuccess.reason)
        assertEquals(1, autoHealer.getSuccessCount())
        assertEquals(0, autoHealer.getFailureCount())

        // Failed re-binding recovery
        val eventFailed = autoHealer.handlePipelineStall("Hardware Disconnect") {
            throw IllegalStateException("Camera hardware disconnected")
        }
        assertFalse(eventFailed.recoverySuccessful)
        assertEquals(1, autoHealer.getFailureCount())
    }
}
