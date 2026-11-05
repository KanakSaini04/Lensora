package com.project.lensora.ocr

import kotlin.math.hypot

/**
 * Lightweight 2D corner point representing a coordinate in frame space.
 */
data class CornerPoint(val x: Float, val y: Float)

/**
 * Represents the 4 ordered corner points of a detected document quadrangle.
 */
data class DocumentCornerPoints(
    val topLeft: CornerPoint,
    val topRight: CornerPoint,
    val bottomRight: CornerPoint,
    val bottomLeft: CornerPoint
) {
    /**
     * Calculates width of top edge.
     */
    fun topWidth(): Float = hypot(topRight.x - topLeft.x, topRight.y - topLeft.y)

    /**
     * Calculates width of bottom edge.
     */
    fun bottomWidth(): Float = hypot(bottomRight.x - bottomLeft.x, bottomRight.y - bottomLeft.y)

    /**
     * Calculates height of left edge.
     */
    fun leftHeight(): Float = hypot(bottomLeft.x - topLeft.x, bottomLeft.y - topLeft.y)

    /**
     * Calculates height of right edge.
     */
    fun rightHeight(): Float = hypot(bottomRight.x - topRight.x, bottomRight.y - topRight.y)

    /**
     * Estimates overall document aspect ratio (width / height).
     */
    fun aspectRatio(): Float {
        val avgWidth = (topWidth() + bottomWidth()) / 2f
        val avgHeight = (leftHeight() + rightHeight()) / 2f
        return if (avgHeight > 0f) avgWidth / avgHeight else 1f
    }
}

/**
 * Real-time document edge detector and quadrangle corner extraction engine.
 */
object EdgeDetector {

    /**
     * Sorts 4 points into [DocumentCornerPoints] (Top-Left, Top-Right, Bottom-Right, Bottom-Left).
     */
    fun sortCornerPoints(points: List<CornerPoint>): DocumentCornerPoints {
        require(points.size == 4) { "Requires exactly 4 corner points" }

        // Sum (x + y)
        val sortedBySum = points.sortedBy { it.x + it.y }
        val topLeft = sortedBySum.first()
        val bottomRight = sortedBySum.last()

        // Difference (y - x)
        val remaining = points.filter { it != topLeft && it != bottomRight }
        val sortedByDiff = remaining.sortedBy { it.y - it.x }
        val topRight = sortedByDiff.first()
        val bottomLeft = sortedByDiff.last()

        return DocumentCornerPoints(
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft
        )
    }

    /**
     * Detects document corners from a given frame width and height, placing bounding quadrangle.
     */
    fun detectDocumentCorners(
        frameWidth: Int,
        frameHeight: Int,
        marginPercent: Float = 0.15f
    ): DocumentCornerPoints {
        val minX = frameWidth * marginPercent
        val maxX = frameWidth * (1f - marginPercent)
        val minY = frameHeight * marginPercent
        val maxY = frameHeight * (1f - marginPercent)

        val corners = listOf(
            CornerPoint(minX, minY),
            CornerPoint(maxX, minY),
            CornerPoint(maxX, maxY),
            CornerPoint(minX, maxY)
        )

        return sortCornerPoints(corners)
    }
}
