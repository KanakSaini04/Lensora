package com.kanak.lensora.ui.tabs

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
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kanak.lensora.camera.LensCapabilities

/**
 * OPTICS Tab: Displays hardware optics specs, aperture, focal length, fixed-focus safety status,
 * and live assistant toggles for False Color, Zebra Stripes, Focus Peaking, Depth Radar, and Starlight Booster.
 */
@Composable
fun OpticsTab(
    capabilities: LensCapabilities,
    modifier: Modifier = Modifier
) {
    var selectedLensIndex by remember { mutableStateOf(1) } // 0: 0.6x Ultrawide, 1: 1x Main, 2: Telephoto/Macro
    val lensNames = listOf("0.6x Ultrawide", "1x Main (200MP)", "Macro / Telephoto")

    // Live Assistant Shader & Radar Toggles
    var focusPeakingActive by remember { mutableStateOf(true) }
    var zebraStripesActive by remember { mutableStateOf(false) }
    var falseColorActive by remember { mutableStateOf(false) }
    var depthRadarActive by remember { mutableStateOf(true) }
    var starlightBoosterActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "OPTICS & ASSISTANT CONTROL",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = "AGSL Shaders & Redmi Note 13 Pro 5G ISOCELL HP3 Module",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                } else {
                    OutlinedButton(
                        onClick = { selectedLensIndex = index },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AGSL Shaders & Radar Toggles Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Shaders", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AGSL GPU SHADER ASSISTANTS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ToggleRow("Neon Cyan Focus Peaking", focusPeakingActive) { focusPeakingActive = it }
                ToggleRow("Zebra Stripes (>94% Overexposure)", zebraStripesActive) { zebraStripesActive = it }
                ToggleRow("ARRI False Color Exposure Map", falseColorActive) { falseColorActive = it }
                ToggleRow("Dual-Axis Gyro Depth Radar", depthRadarActive) { depthRadarActive = it }
                ToggleRow("Starlight Night-Vision Booster", starlightBoosterActive) { starlightBoosterActive = it }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fixed Focus Safety Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (capabilities.canOverrideFocusManually()) "MANUAL OVERRIDE READY" else "FIXED FOCUS SAFETY LOCK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Text(
                        text = if (capabilities.canOverrideFocusManually())
                            "Lens supports manual diopter overrides without risk of crash."
                        else
                            "Auxiliary lens is fixed focus. Manual focus diopter override safely disabled.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Optical Parameters Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Params", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HARDWARE SPECIFICATIONS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
