package com.kanak.lensora

import com.kanak.lensora.quantum.MultiFrameFusionEngine
import com.kanak.lensora.quantum.ZslFrameMetadata
import com.kanak.lensora.quantum.ZslRingBuffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuantumBufferTest {

    @Test
    fun testZslRingBufferEviction() {
        val buffer = ZslRingBuffer(capacity = 3)

        buffer.addFrame(ZslFrameMetadata(100L, 100, 1000L, 1L))
        buffer.addFrame(ZslFrameMetadata(200L, 200, 2000L, 2L))
        buffer.addFrame(ZslFrameMetadata(300L, 400, 3000L, 3L))

        assertEquals(3, buffer.size)

        // Adding 4th frame should evict oldest frame (frame 1)
        buffer.addFrame(ZslFrameMetadata(400L, 800, 4000L, 4L))
        assertEquals(3, buffer.size)

        val frames = buffer.getLatestFrames()
        assertEquals(2L, frames.first().frameId)
        assertEquals(4L, frames.last().frameId)
    }

    @Test
    fun testPreCapture05sTimeWindowFiltering() {
        val buffer = ZslRingBuffer(capacity = 16, timeWindowNs = 500_000_000L) // 500ms

        val baseNs = 1_000_000_000L // 1.0s

        buffer.addFrame(ZslFrameMetadata(baseNs - 700_000_000L, 100, 1000L, 1L)) // 0.3s (too old)
        buffer.addFrame(ZslFrameMetadata(baseNs - 400_000_000L, 100, 1000L, 2L)) // 0.6s (within 0.5s)
        buffer.addFrame(ZslFrameMetadata(baseNs - 100_000_000L, 100, 1000L, 3L)) // 0.9s (within 0.5s)
        buffer.addFrame(ZslFrameMetadata(baseNs, 100, 1000L, 4L))                 // 1.0s (shutter moment)

        val preCaptureFrames = buffer.getPreCaptureFramesForShutter(shutterTimestampNs = baseNs)

        // Should return frames 2, 3, 4 (3 frames within 0.5s window)
        assertEquals(3, preCaptureFrames.size)
        assertEquals(2L, preCaptureFrames.first().frameId)
        assertEquals(4L, preCaptureFrames.last().frameId)
    }

    @Test
    fun testMultiFrameGyroFusionEngine() {
        val gyroShift = MultiFrameFusionEngine.calculateGyroSubpixelShift(
            pitchVelocityRad = 0.05f,
            rollVelocityRad = 0.02f,
            focalLengthMm = 23.0f
        )

        assertEquals(0.046f, gyroShift.dxSubpixel, 0.001f)
        assertEquals(0.115f, gyroShift.dySubpixel, 0.001f)

        // 4-frame burst fusion
        val burstFrames = listOf(
            ZslFrameMetadata(100L, 100, 1000L, 101L),
            ZslFrameMetadata(200L, 100, 1000L, 102L),
            ZslFrameMetadata(300L, 100, 1000L, 103L),
            ZslFrameMetadata(400L, 100, 1000L, 104L)
        )

        val result = MultiFrameFusionEngine.fuseFrames(burstFrames, gyroShift)

        assertEquals(104L, result.baseFrameId)
        assertEquals(4, result.totalFramesMerged)
        assertEquals(0.5f, result.noiseReductionRatio, 0.01f) // 1 / sqrt(4) = 0.5
        assertEquals(6.02f, result.snrGainDb, 0.05f)           // 10 * log10(4) = ~6.02 dB
        assertTrue(result.isSuperResOutput)
    }
}
