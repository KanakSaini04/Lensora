package com.kanak.lensora

import app.cash.turbine.test
import com.kanak.lensora.optics.HapticDialState
import com.kanak.lensora.optics.SensorTelemetry
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class OpticsTest {

    @Test
    fun testSensorTelemetryFlow() = runTest {
        val telemetry = SensorTelemetry()
        telemetry.telemetryState.test {
            val initial = awaitItem()
            assertEquals(100, initial.iso)

            telemetry.updateTelemetry(400, 2_000_000L, 5.0f)
            val updated = awaitItem()
            assertEquals(400, updated.iso)
            assertEquals(2_000_000L, updated.exposureTimeNs)
            assertEquals(5.0f, updated.focusDiopters, 0.01f)
        }
    }

    @Test
    fun testHapticDialState() = runTest {
        val dialState = HapticDialState()
        dialState.isoDial.test {
            val initial = awaitItem()
            assertEquals(100f, initial.currentValue, 0.01f)

            dialState.updateIso(800f)
            val updated = awaitItem()
            assertEquals(800f, updated.currentValue, 0.01f)
        }
    }
}
