package com.kanak.lensora.quantum

/**
 * Metadata holding timestamp, ISO, and exposure info for zero-shutter-lag pre-capture buffer frames.
 */
data class ZslFrameMetadata(
    val timestampNs: Long,
    val iso: Int,
    val exposureNs: Long,
    val frameId: Long
)

/**
 * High-performance circular ring buffer managing pre-capture frames with zero memory allocation churn.
 */
class ZslRingBuffer(private val capacity: Int = 8) {
    private val buffer = ArrayDeque<ZslFrameMetadata>(capacity)

    @Synchronized
    fun addFrame(frame: ZslFrameMetadata) {
        if (buffer.size >= capacity) {
            buffer.removeFirst()
        }
        buffer.addLast(frame)
    }

    @Synchronized
    fun getLatestFrames(): List<ZslFrameMetadata> {
        return buffer.toList()
    }

    @Synchronized
    fun clear() {
        buffer.clear()
    }

    val size: Int
        @Synchronized get() = buffer.size
}
