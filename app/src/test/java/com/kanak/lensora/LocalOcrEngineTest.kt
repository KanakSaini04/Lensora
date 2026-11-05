package com.kanak.lensora

import com.kanak.lensora.ocr.LocalOcrEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalOcrEngineTest {

    @Test
    fun testAadhaarMasking() {
        val rawText = "Government of India Unique Identification Aadhaar 1234 5678 9012 DOB 01/01/2000"
        val result = LocalOcrEngine.extractKycFields(rawText)

        assertEquals("Aadhaar Card", result.documentType)
        assertEquals("XXXX XXXX 9012", result.maskedIdNumber)
        assertEquals("01/01/2000", result.dob)
        assertTrue(result.isPiiSanitized)
    }

    @Test
    fun testPanMasking() {
        val rawText = "INCOME TAX DEPARTMENT GOVT OF INDIA ABCDE1234F DOB 10/10/1990"
        val result = LocalOcrEngine.extractKycFields(rawText)

        assertEquals("PAN Card", result.documentType)
        assertEquals("XXXXX1234F", result.maskedIdNumber)
        assertEquals("10/10/1990", result.dob)
        assertTrue(result.isPiiSanitized)
    }
}
