package com.project.lensora.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * NEURAL Tab: Edge-AI Document Sanitization, PII Masking & Local OCR Engine.
 */
@Composable
fun NeuralTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Psychology, contentDescription = "Neural", tint = Color(0xFFA855F7))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NEURAL ENGINE • EDGE-AI",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
        Text(
            text = "Local Document Sanitization & PII Detection Pipeline",
            fontSize = 12.sp,
            color = Color.Gray,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Active", tint = Color(0xFFA855F7))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ON-DEVICE INFERENCE ENGINE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                NeuralStatusRow("MediaPipe Tasks Vision", "Ready (Target 0ms latency)")
                NeuralStatusRow("Aadhaar / PII Masking", "Active (Local Zero-Data Leak)")
                NeuralStatusRow("Gemini Nano / Tesseract OCR", "Standby for Document Frame")
                NeuralStatusRow("OpenCV Contour Scanner", "Pre-Allocated Buffer Ready")
            }
        }
    }
}

@Composable
private fun NeuralStatusRow(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "• $label: ", color = Color.LightGray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Text(text = status, color = Color(0xFFC084FC), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
