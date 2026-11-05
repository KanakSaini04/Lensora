package com.project.lensora.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.lensora.camera.LensCapabilities

/**
 * OPTICS Tab: Displays hardware optics specs, aperture, focal length, and fixed-focus safety status.
 */
@Composable
fun OpticsTab(
    capabilities: LensCapabilities,
    modifier: Modifier = Modifier
) {
    var selectedLensIndex by remember { mutableStateOf(1) } // 0: 0.6x Ultrawide, 1: 1x Main, 2: Telephoto/Macro
    val lensNames = listOf("0.6x Ultrawide", "1x Main (200MP)", "Macro / Telephoto")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "OPTICS & LENS INSPECTOR",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        )
        Text(
            text = "Redmi Note 13 Pro 5G ISOCELL HP3 Module Telemetry",
            fontSize = 12.sp,
            color = Color.Gray,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lens Selector Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            lensNames.forEachIndexed { index, name ->
                val isSelected = index == selectedLensIndex
                if (isSelected) {
                    Button(
                        onClick = { selectedLensIndex = index },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                } else {
                    OutlinedButton(
                        onClick = { selectedLensIndex = index },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(name, fontSize = 10.sp, color = Color.White, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fixed Focus Safety Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (capabilities.canOverrideFocusManually()) Color(0xFF064E3B) else Color(0xFF4C1D95)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (capabilities.canOverrideFocusManually()) Icons.Default.CheckCircle else Icons.Default.Lock,
                    contentDescription = "Safety Status",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (capabilities.canOverrideFocusManually()) "MANUAL OVERRIDE READY" else "FIXED FOCUS SAFETY LOCK",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (capabilities.canOverrideFocusManually())
                            "Lens supports manual diopter overrides without risk of crash."
                        else
                            "Auxiliary lens is fixed focus. Manual focus diopter override safely disabled.",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Optical Parameters Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Params", tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HARDWARE SPECIFICATIONS",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OpticsDetailRow(
                    label = "Focal Lengths",
                    value = if (capabilities.focalLengths.isNotEmpty()) {
                        capabilities.focalLengths.joinToString(" mm, ") + " mm"
                    } else "23.0 mm (1x Eq.)"
                )

                OpticsDetailRow(
                    label = "Available Apertures",
                    value = if (capabilities.availableApertures.isNotEmpty()) {
                        capabilities.availableApertures.joinToString(", ") { "f/$it" }
                    } else "f/1.65 (HP3 Main)"
                )

                OpticsDetailRow(
                    label = "Min Focus Distance",
                    value = capabilities.minFocusDistanceDiopters?.let {
                        if (it > 0f) "${"%.2f".format(it)} Diopters (${"%.1f".format(100f / it)} cm)" else "Fixed Focus (0.0 D)"
                    } ?: "10.0 cm Macro Focus"
                )

                OpticsDetailRow(
                    label = "Fixed Focus Lens",
                    value = if (capabilities.isFixedFocus) "Yes" else "No (Variable Focus)"
                )

                OpticsDetailRow(
                    label = "Manual Focus Support",
                    value = if (capabilities.supportsManualFocus) "Supported" else "Disabled / Auto Only"
                )
            }
        }
    }
}

@Composable
private fun OpticsDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
    }
}
