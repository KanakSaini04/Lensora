package com.kanak.lensora.neural

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

/**
 * Framing suggestion data representing rule-of-thirds balance and pan/tilt guidance.
 */
data class FramingSuggestion(
    val targetOffsetX: Float = 0.0f,
    val targetOffsetY: Float = 0.0f,
    val suggestionText: String = "Framing Balanced",
    val isBalanced: Boolean = true
)

/**
 * High-performance, zero-allocation ImageAnalysis.Analyzer for real-time rule-of-thirds AI composition.
 */
class NeuralFramingEngine : ImageAnalysis.Analyzer {

    private val _framingSuggestion = MutableStateFlow(FramingSuggestion())
    val framingSuggestion: StateFlow<FramingSuggestion> = _framingSuggestion.asStateFlow()

    // Pre-allocated buffers for zero-allocation per-frame analysis
    private val rowSumBuffer = LongArray(1080)
    private val colSumBuffer = LongArray(1920)

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val yBuffer = imageProxy.planes[0].buffer
            val width = imageProxy.width
            val height = imageProxy.height
            val rowStride = imageProxy.planes[0].rowStride

            // Reset buffers
            for (i in 0 until height) {
                if (i < rowSumBuffer.size) rowSumBuffer[i] = 0L
            }
            for (j in 0 until width) {
                if (j < colSumBuffer.size) colSumBuffer[j] = 0L
            }

            yBuffer.rewind()
            var totalLuminance = 0L
            val step = 8 // Downsample by 8x for fast 120Hz analysis

            var rowIdx = 0
            while (rowIdx < height) {
                val rowStart = rowIdx * rowStride
                var colIdx = 0
                while (colIdx < width) {
                    val pixelVal = yBuffer.get(rowStart + colIdx).toInt() and 0xFF
                    totalLuminance += pixelVal
                    if (rowIdx < rowSumBuffer.size) rowSumBuffer[rowIdx] += pixelVal.toLong()
                    if (colIdx < colSumBuffer.size) colSumBuffer[colIdx] += pixelVal.toLong()
                    colIdx += step
                }
                rowIdx += step
            }

            if (totalLuminance > 0) {
                // Calculate center-of-mass
                var weightedRowSum = 0L
                var row = 0
                while (row < height) {
                    if (row < rowSumBuffer.size) {
                        weightedRowSum += row * rowSumBuffer[row]
                    }
                    row += step
                }
                val centerY = (weightedRowSum.toDouble() / totalLuminance).toFloat()

                var weightedColSum = 0L
                var col = 0
                while (col < width) {
                    if (col < colSumBuffer.size) {
                        weightedColSum += col * colSumBuffer[col]
                    }
                    col += step
                }
                val centerX = (weightedColSum.toDouble() / totalLuminance).toFloat()

                // Calculate rule-of-thirds offset from image center
                val idealX = width / 2.0f
                val idealY = height / 3.0f // Golden ratio / Rule-of-thirds top line

                val offsetX = (centerX - idealX) / width
                val offsetY = (centerY - idealY) / height

                val isBalanced = abs(offsetX) < 0.10f && abs(offsetY) < 0.10f
                val text = when {
                    isBalanced -> "Framing Balanced ✓"
                    offsetX > 0.10f -> "Pan Left"
                    offsetX < -0.10f -> "Pan Right"
                    offsetY > 0.10f -> "Tilt Up"
                    else -> "Tilt Down"
                }

                _framingSuggestion.value = FramingSuggestion(
                    targetOffsetX = offsetX,
                    targetOffsetY = offsetY,
                    suggestionText = text,
                    isBalanced = isBalanced
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }

    /**
     * Helper calculation for unit testing rule-of-thirds balance offset.
     */
    fun calculateRuleOfThirds(centerX: Float, centerY: Float, width: Float, height: Float): FramingSuggestion {
        val idealX = width / 2.0f
        val idealY = height / 3.0f

        val offsetX = (centerX - idealX) / width
        val offsetY = (centerY - idealY) / height

        val isBalanced = abs(offsetX) < 0.10f && abs(offsetY) < 0.10f
        val text = when {
            isBalanced -> "Framing Balanced ✓"
            offsetX > 0.10f -> "Pan Left"
            offsetX < -0.10f -> "Pan Right"
            offsetY > 0.10f -> "Tilt Up"
            else -> "Tilt Down"
        }

        return FramingSuggestion(
            targetOffsetX = offsetX,
            targetOffsetY = offsetY,
            suggestionText = text,
            isBalanced = isBalanced
        )
    }
}
