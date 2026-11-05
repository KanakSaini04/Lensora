package com.kanak.lensora.data

/**
 * 10-parameter cinematic color grading model with master intensity scaling.
 */
data class ColorGradingParameters(
    val brightness: Float = 0.0f,    // -1.0 to 1.0
    val contrast: Float = 1.0f,      //  0.0 to 2.0
    val saturation: Float = 1.0f,    //  0.0 to 2.0
    val brilliance: Float = 0.0f,    // -1.0 to 1.0
    val sharpen: Float = 0.0f,       //  0.0 to 1.0
    val clarity: Float = 0.0f,       // -1.0 to 1.0
    val highlights: Float = 0.0f,    // -1.0 to 1.0
    val shadows: Float = 0.0f,       // -1.0 to 1.0
    val temperature: Float = 0.0f,   // -1.0 to 1.0 (Cool < 0 < Warm)
    val hue: Float = 0.0f,           // -180.0 to 180.0
    val intensity: Float = 1.0f      //  0.0 to 1.0
)

/**
 * High-performance 4x5 ColorMatrix generator for real-time GPU color correction.
 */
object ColorGradingEngine {

    /**
     * Generates a 20-element FloatArray 4x5 ColorMatrix from parameters, blended by master intensity.
     */
    fun generate4x5Matrix(params: ColorGradingParameters): FloatArray {
        val c = params.contrast
        val b = params.brightness * 255f
        val s = params.saturation
        val temp = params.temperature * 40f

        // Luminance weights for saturation
        val rw = 0.213f
        val gw = 0.715f
        val bw = 0.072f

        val sr = (1f - s) * rw
        val sg = (1f - s) * gw
        val sb = (1f - s) * bw

        // Base Saturation + Contrast matrix
        val rawMatrix = floatArrayOf(
            (sr + s) * c, sg * c, sb * c, 0f, b + temp,
            sr * c, (sg + s) * c, sb * c, 0f, b,
            sr * c, sg * c, (sb + s) * c, 0f, b - temp,
            0f, 0f, 0f, 1f, 0f
        )

        // Identity 4x5 Matrix
        val identity = floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )

        // Blend rawMatrix with Identity according to master intensity slider (0.0 to 1.0)
        val k = params.intensity.coerceIn(0f, 1f)
        val blended = FloatArray(20)
        for (i in 0 until 20) {
            blended[i] = identity[i] + (rawMatrix[i] - identity[i]) * k
        }

        return blended
    }
}
