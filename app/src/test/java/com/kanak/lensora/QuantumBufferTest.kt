package com.kanak.lensora

import com.kanak.lensora.quantum.ZslFrameMetadata
import com.kanak.lensora.quantum.ZslRingBuffer
import org.junit.Assert.assertEquals
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
}
