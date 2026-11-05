package com.project.lensora.ocr

import android.graphics.Matrix

/**
 * Perspective warp transformation engine that unwarps skewed document quadrangle images into clean rectangular Bitmaps.
 */
object DocumentTransformer {

    data class OutputDimensions(val width: Int, val height: Int)

    /**
     * Calculates the target rectangular output dimensions from quadrangle corners.
     */
    fun calculateOutputDimensions(corners: DocumentCornerPoints): OutputDimensions {
        val targetWidth = maxOf(corners.topWidth(), corners.bottomWidth()).toInt().coerceAtLeast(1)
        val targetHeight = maxOf(corners.leftHeight(), corners.rightHeight()).toInt().coerceAtLeast(1)
        return OutputDimensions(targetWidth, targetHeight)
    }

    /**
     * Computes the 3x3 perspective transformation matrix from source quadrangle corners to target rectangle.
     */
    fun computePerspectiveMatrix(corners: DocumentCornerPoints): Matrix {
        val dims = calculateOutputDimensions(corners)

        val srcPoints = floatArrayOf(
            corners.topLeft.x, corners.topLeft.y,
            corners.topRight.x, corners.topRight.y,
            corners.bottomRight.x, corners.bottomRight.y,
            corners.bottomLeft.x, corners.bottomLeft.y
        )

        val dstPoints = floatArrayOf(
            0f, 0f,
            dims.width.toFloat(), 0f,
            dims.width.toFloat(), dims.height.toFloat(),
            0f, dims.height.toFloat()
        )

        val matrix = Matrix()
        matrix.setPolyToPoly(srcPoints, 0, dstPoints, 0, 4)
        return matrix
    }
}
