package com.project.lensora

import com.project.lensora.ocr.CornerPoint
import com.project.lensora.ocr.DocumentCornerPoints
import com.project.lensora.ocr.DocumentTransformer
import com.project.lensora.ocr.EdgeDetector
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentProcessingTest {

    @Test
    fun testCornerSorting() {
        val unsorted = listOf(
            CornerPoint(800f, 600f), // Bottom-Right
            CornerPoint(100f, 100f), // Top-Left
            CornerPoint(100f, 600f), // Bottom-Left
            CornerPoint(800f, 100f)  // Top-Right
        )

        val corners = EdgeDetector.sortCornerPoints(unsorted)

        assertEquals(100f, corners.topLeft.x, 0.01f)
        assertEquals(100f, corners.topLeft.y, 0.01f)

        assertEquals(800f, corners.topRight.x, 0.01f)
        assertEquals(100f, corners.topRight.y, 0.01f)

        assertEquals(800f, corners.bottomRight.x, 0.01f)
        assertEquals(600f, corners.bottomRight.y, 0.01f)

        assertEquals(100f, corners.bottomLeft.x, 0.01f)
        assertEquals(600f, corners.bottomLeft.y, 0.01f)
    }

    @Test
    fun testOutputDimensionCalculation() {
        val corners = DocumentCornerPoints(
            topLeft = CornerPoint(0f, 0f),
            topRight = CornerPoint(856f, 0f),
            bottomRight = CornerPoint(856f, 540f),
            bottomLeft = CornerPoint(0f, 540f)
        )

        val dims = DocumentTransformer.calculateOutputDimensions(corners)
        assertEquals(856, dims.width)
        assertEquals(540, dims.height)
    }
}
