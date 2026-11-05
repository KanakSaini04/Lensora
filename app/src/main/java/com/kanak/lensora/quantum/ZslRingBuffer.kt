package com.kanak.lensora.quantum

/**
 * Metadata holding timestamp, ISO, exposure, and optional raw buffer for ZSL pre-capture frames.
 */
data class ZslFrameMetadata(
    val timestampNs: Long,
    val iso: Int,
    val exposureNs: Long,
    val frameId: Long,
    val yuvData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ZslFrameMetadata

        if (timestampNs != other.timestampNs) return false
        if (iso != other.iso) return false
        if (exposureNs != other.exposureNs) return false
        if (frameId != other.frameId) return false
        if (yuvData != null) {
            if (other.yuvData == null) return false
            if (!yuvData.contentEquals(other.yuvData)) return false
        } else if (other.yuvData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestampNs.hashCode()
        result = 31 * result + iso
        result = 31 * result + exposureNs.hashCode()
        result = 31 * result + frameId.hashCode()
        result = 31 * result + (yuvData?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * High-performance circular RAM ring buffer managing 0.5-second (500ms) pre-capture frames
 * for Zero-Shutter-Lag (ZSL) decisive moment capture with zero memory allocation churn.
 */
class ZslRingBuffer(
    private val capacity: Int = 16,
    val timeWindowNs: Long = 500_000_000L // 0.5 Seconds in nanoseconds
) {
    private val buffer = ArrayDeque<ZslFrameMetadata>(capacity)

    @Synchronized
    fun addFrame(frame: ZslFrameMetadata) {
        // Enforce capacity bound
        if (buffer.size >= capacity) {
            buffer.removeFirst()
        }
        buffer.addLast(frame)

        // Purge frames older than 0.5 seconds relative to newest frame
        val latestTimestamp = frame.timestampNs
        while (buffer.isNotEmpty() && (latestTimestamp - buffer.first().timestampNs) > timeWindowNs) {
            buffer.removeFirst()
        }
    }

    /**
     * Retrieves all pre-capture frames recorded within 0.5s prior to shutter button press.
     */
    @Synchronized
    fun getPreCaptureFramesForShutter(shutterTimestampNs: Long): List<ZslFrameMetadata> {
        val minValidTimestamp = shutterTimestampNs - timeWindowNs
        return buffer.filter { it.timestampNs in minValidTimestamp..shutterTimestampNs }
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
