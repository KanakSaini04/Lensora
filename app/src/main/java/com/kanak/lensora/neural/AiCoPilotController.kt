package com.kanak.lensora.neural

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CoPilotMode {
    CYBER_AUTO,
    HYBRID_SMART_LOCK,
    TACTICAL_MANUAL
}

enum class CameraControlParameter {
    ISO,
    EXPOSURE_TIME,
    FOCUS_DIOPTER,
    WHITE_BALANCE
}

data class CoPilotState(
    val mode: CoPilotMode = CoPilotMode.CYBER_AUTO,
    val lockedParameters: Set<CameraControlParameter> = emptySet(),
    val targetIso: Int = 100,
    val targetExposureNs: Long = 1_000_000L,
    val targetFocusDiopters: Float = 0.0f
)

/**
 * 3-Way Master Switch Controller managing AI Autopilot, Hybrid Smart-Lock, and Tactical Manual camera controls.
 */
class AiCoPilotController {

    private val _state = MutableStateFlow(CoPilotState())
    val state: StateFlow<CoPilotState> = _state.asStateFlow()

    fun setMode(mode: CoPilotMode) {
        _state.value = _state.value.copy(mode = mode)
    }

    fun lockParameter(param: CameraControlParameter) {
        val updatedLocks = _state.value.lockedParameters + param
        val newMode = if (_state.value.mode == CoPilotMode.CYBER_AUTO) {
            CoPilotMode.HYBRID_SMART_LOCK
        } else {
            _state.value.mode
        }
        _state.value = _state.value.copy(
            lockedParameters = updatedLocks,
            mode = newMode
        )
    }

    fun unlockParameter(param: CameraControlParameter) {
        val updatedLocks = _state.value.lockedParameters - param
        val newMode = if (updatedLocks.isEmpty() && _state.value.mode == CoPilotMode.HYBRID_SMART_LOCK) {
            CoPilotMode.CYBER_AUTO
        } else {
            _state.value.mode
        }
        _state.value = _state.value.copy(
            lockedParameters = updatedLocks,
            mode = newMode
        )
    }

    fun isParameterLocked(param: CameraControlParameter): Boolean {
        if (_state.value.mode == CoPilotMode.TACTICAL_MANUAL) return true
        if (_state.value.mode == CoPilotMode.CYBER_AUTO) return false
        return _state.value.lockedParameters.contains(param)
    }

    fun applyAiSuggestion(aiIso: Int, aiExposureNs: Long, aiFocusDiopters: Float) {
        val currentState = _state.value
        if (currentState.mode == CoPilotMode.TACTICAL_MANUAL) return

        val newIso = if (isParameterLocked(CameraControlParameter.ISO)) currentState.targetIso else aiIso
        val newExposure = if (isParameterLocked(CameraControlParameter.EXPOSURE_TIME)) currentState.targetExposureNs else aiExposureNs
        val newFocus = if (isParameterLocked(CameraControlParameter.FOCUS_DIOPTER)) currentState.targetFocusDiopters else aiFocusDiopters

        _state.value = currentState.copy(
            targetIso = newIso,
            targetExposureNs = newExposure,
            targetFocusDiopters = newFocus
        )
    }
}
