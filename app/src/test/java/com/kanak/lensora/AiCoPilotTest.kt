package com.kanak.lensora

import app.cash.turbine.test
import com.kanak.lensora.neural.AiCoPilotController
import com.kanak.lensora.neural.CameraControlParameter
import com.kanak.lensora.neural.CameraParameterSmoother
import com.kanak.lensora.neural.CoPilotMode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiCoPilotTest {

    @Test
    fun testModeSwitchingAndParameterLocking() = runTest {
        val controller = AiCoPilotController()

        controller.state.test {
            val initial = awaitItem()
            assertEquals(CoPilotMode.CYBER_AUTO, initial.mode)
            assertFalse(controller.isParameterLocked(CameraControlParameter.ISO))

            // Lock ISO dial -> automatically moves to HYBRID_SMART_LOCK
            controller.lockParameter(CameraControlParameter.ISO)
            val hybridState = awaitItem()
            assertEquals(CoPilotMode.HYBRID_SMART_LOCK, hybridState.mode)
            assertTrue(controller.isParameterLocked(CameraControlParameter.ISO))
            assertFalse(controller.isParameterLocked(CameraControlParameter.EXPOSURE_TIME))

            // Apply AI suggestion: ISO should remain locked at 100, while exposure time updates to 2000000L
            controller.applyAiSuggestion(aiIso = 800, aiExposureNs = 2_000_000L, aiFocusDiopters = 5.0f)
            val updatedState = awaitItem()
            assertEquals(100, updatedState.targetIso)
            assertEquals(2_000_000L, updatedState.targetExposureNs)

            // Unlock ISO -> mode transitions back to CYBER_AUTO
            controller.unlockParameter(CameraControlParameter.ISO)
            val autoState = awaitItem()
            assertEquals(CoPilotMode.CYBER_AUTO, autoState.mode)
            assertFalse(controller.isParameterLocked(CameraControlParameter.ISO))
        }
    }

    @Test
    fun testEmaSmoothDampingAndDeadbandFiltering() {
        val smoother = CameraParameterSmoother(alpha = 0.2f, deadbandPercent = 0.04f)

        val current = 100f

        // 2% change (target = 102) is within 4% deadband -> returns current value (100f)
        val withinDeadband = smoother.smoothValue(targetValue = 102f, currentValue = current)
        assertEquals(100f, withinDeadband, 0.001f)

        // 10% change (target = 110) is above deadband -> smoothed = 0.2*110 + 0.8*100 = 102f
        val aboveDeadband = smoother.smoothValue(targetValue = 110f, currentValue = current)
        assertEquals(102f, aboveDeadband, 0.001f)
    }
}
