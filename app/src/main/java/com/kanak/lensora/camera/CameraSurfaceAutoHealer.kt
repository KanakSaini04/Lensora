package com.kanak.lensora.camera

/**
 * Log entry capturing a camera pipeline self-healing recovery event.
 */
data class HealingEvent(
    val timestampNs: Long = System.nanoTime(),
    val reason: String,
    val recoverySuccessful: Boolean
)

/**
 * Resilient auto-patch exception recovery manager for Camera2Interop surface re-binding
 * during thermal throttling, buffer stalls, or resource pressure.
 */
class CameraSurfaceAutoHealer {

    private val _healingHistory = mutableListOf<HealingEvent>()
    val healingHistory: List<HealingEvent>
        get() = _healingHistory.toList()

    /**
     * Executes auto-patch surface re-binding recovery action when a pipeline stall or exception is detected.
     */
    fun handlePipelineStall(reason: String, rebindAction: () -> Boolean): HealingEvent {
        val success = try {
            rebindAction()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

        val event = HealingEvent(
            timestampNs = System.nanoTime(),
            reason = reason,
            recoverySuccessful = success
        )
        _healingHistory.add(event)
        return event
    }

    fun getSuccessCount(): Int {
        return _healingHistory.count { it.recoverySuccessful }
    }

    fun getFailureCount(): Int {
        return _healingHistory.count { !it.recoverySuccessful }
    }

    fun clearHistory() {
        _healingHistory.clear()
    }
}
