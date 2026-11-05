package com.kanak.lensora.ui.tabs

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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Shield
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
 * QUANTUM Tab: Purpose-Bound Watermarking & Secure KYC Cryptographic Verification Engine.
 */
@Composable
fun QuantumTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = "Quantum", tint = Color(0xFF06B6D4))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "QUANTUM SECURITY • WATERMARK",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
        Text(
            text = "Purpose-Bound Watermarking & Tamper-Proof Digital KYC Engine",
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
                    Icon(imageVector = Icons.Default.Fingerprint, contentDescription = "Cryptographic", tint = Color(0xFF06B6D4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PURPOSE-BOUND CRYPTOGRAPHY",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                QuantumStatusRow("Watermark Intent", "Digital KYC Verification Only")
                QuantumStatusRow("Temporal Timestamp", "Crypto-Hashed Device Epoch")
                QuantumStatusRow("Steganographic Layer", "DWT-DCT Hybrid Frequency Domain")
                QuantumStatusRow("Tamper Protection", "Hardware KeyStore Signed")
            }
        }
    }
}

@Composable
private fun QuantumStatusRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "• $label: ", color = Color.LightGray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = Color(0xFF22D3EE), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
