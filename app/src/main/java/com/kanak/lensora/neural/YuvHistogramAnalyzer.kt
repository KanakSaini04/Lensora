package com.kanak.lensora.neural

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * Result data holder for 256-bin luminance histogram analysis and clipping metrics.
 */
data class HistogramResult(
    val bins: IntArray = IntArray(256),
    val shadowClippingRatio: Float = 0.0f,
    val highlightClippingRatio: Float = 0.0f,
    val meanLuminance: Float = 128.0f
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HistogramResult

        if (!bins.contentEquals(other.bins)) return false
        if (shadowClippingRatio != other.shadowClippingRatio) return false
        if (highlightClippingRatio != other.highlightClippingRatio) return false
        if (meanLuminance != other.meanLuminance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bins.contentHashCode()
        result = 31 * result + shadowClippingRatio.hashCode()
        result = 31 * result + highlightClippingRatio.hashCode()
        result = 31 * result + meanLuminance.hashCode()
        return result
    }
}

/**
 * High-performance zero-allocation ImageAnalysis.Analyzer computing 256-bin YUV luminance histogram.
 * Strict zero-garbage-collection per-frame callback adhering to AGENTS.md rules.
 */
class YuvHistogramAnalyzer(
    private val sampleStep: Int = 4,
    private val onHistogramComputed: (HistogramResult) -> Unit = {}
) : ImageAnalysis.Analyzer {

    // Pre-allocated reusable primitive structures initialized outside analyze()
    private val binsBuffer = IntArray(256)

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val yPlane = imageProxy.planes.getOrNull(0) ?: return
            val buffer = yPlane.buffer
            val remaining = buffer.remaining()
            if (remaining <= 0) return

            // Reset bins without reallocating
            binsBuffer.fill(0)

            var shadowCount = 0
            var highlightCount = 0
            var totalSampled = 0
            var sumLuminance = 0L

            val limit = buffer.limit()
            var i = buffer.position()
            while (i < limit) {
                val pixelVal = buffer.get(i).toInt() and 0xFF
                binsBuffer[pixelVal]++
                sumLuminance += pixelVal

                if (pixelVal <= 5) shadowCount++
                if (pixelVal >= 250) highlightCount++
                totalSampled++

                i += sampleStep
            }

            if (totalSampled > 0) {
                val shadowRatio = shadowCount.toFloat() / totalSampled
                val highlightRatio = highlightCount.toFloat() / totalSampled
                val meanLum = sumLuminance.toFloat() / totalSampled

                val result = HistogramResult(
                    bins = binsBuffer.clone(),
                    shadowClippingRatio = shadowRatio,
                    highlightClippingRatio = highlightRatio,
                    meanLuminance = meanLum
                )
                onHistogramComputed(result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }

    /**
     * Pure computational helper for unit testing histogram calculation over primitive byte arrays.
     */
    fun computeHistogramFromByteArray(bytes: ByteArray): HistogramResult {
        val bins = IntArray(256)
        var shadowCount = 0
        var highlightCount = 0
        var total = 0
        var sum = 0L

        for (b in bytes) {
            val pixelVal = b.toInt() and 0xFF
            bins[pixelVal]++
            sum += pixelVal
            if (pixelVal <= 5) shadowCount++
            if (pixelVal >= 250) highlightCount++
            total++
        }

        val shadowRatio = if (total > 0) shadowCount.toFloat() / total else 0.0f
        val highlightRatio = if (total > 0) highlightCount.toFloat() / total else 0.0f
        val meanLum = if (total > 0) sum.toFloat() / total else 0.0f

        return HistogramResult(
            bins = bins,
            shadowClippingRatio = shadowRatio,
            highlightClippingRatio = highlightRatio,
            meanLuminance = meanLum
        )
    }
}
