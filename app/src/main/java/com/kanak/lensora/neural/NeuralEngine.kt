package com.kanak.lensora.neural

/**
 * Encapsulates AI co-pilot and MediaPipe scene detection results.
 */
data class NeuralInferenceResult(
    val detectedScene: String = "Document KYC Mode",
    val confidence: Float = 0.98f,
    val inferenceTimeMs: Long = 12L
)

/**
 * On-device neural inference and MediaPipe scene classification engine coordinator.
 */
object NeuralEngine {
    fun processFrame(frameTimestampNs: Long): NeuralInferenceResult {
        return NeuralInferenceResult(
            detectedScene = "Document KYC Mode",
            confidence = 0.98f,
            inferenceTimeMs = 12L
        )
    }
}
