package com.kanak.lensora

import com.kanak.lensora.ocr.CornerPoint
import com.kanak.lensora.ocr.DocumentCornerPoints
import com.kanak.lensora.ocr.DocumentTransformer
import com.kanak.lensora.ocr.EdgeDetector
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentProcessingTest {

    @Test
    fun testCornerSorting() {
        val unsorted = listOf(
            CornerPoint(800f, 600f),
            CornerPoint(100f, 100f),
            CornerPoint(100f, 600f),
            CornerPoint(800f, 100f)
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
