package com.kanak.lensora.ui.tabs

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kanak.lensora.camera.HardwareCapabilitiesInspector
import com.kanak.lensora.camera.LensCapabilities
import com.kanak.lensora.optics.HapticDialState
import com.kanak.lensora.ui.camera.CameraPreviewView

/**
 * RIG Tab: Full-screen Camera Viewfinder with 120Hz deferred Compose state read telemetry HUD
 * and 2160Hz tactile haptic rotary scrubbers for ISO, Shutter Speed, Kelvin WB, and Focus Diopters.
 */
@Composable
fun RigTab(
    modifier: Modifier = Modifier,
    onCapabilitiesUpdated: (LensCapabilities) -> Unit = {}
) {
    var capabilities by remember { mutableStateOf(LensCapabilities()) }
    val hapticDialState = remember { HapticDialState() }
    val hapticFeedback = LocalHapticFeedback.current

    var currentIso by remember { mutableFloatStateOf(100f) }
    var currentShutter by remember { mutableFloatStateOf(125f) }
    var currentKelvin by remember { mutableFloatStateOf(5500f) }
    var currentFocusDiopter by remember { mutableFloatStateOf(0.0f) }

    Box(modifier = modifier.fillMaxSize()) {
        // Camera Viewfinder Stream
        CameraPreviewView(
            modifier = Modifier.fillMaxSize(),
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            onCameraInitialized = { camera: Camera ->
                val cap = HardwareCapabilitiesInspector.inspect(camera.cameraInfo)
                capabilities = cap
                onCapabilitiesUpdated(cap)
            }
        )

        // Telemetry & Tactile Scrubbers HUD Overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        shadowElevation = 8f
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = "Sensor",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RIG TELEMETRY • ISOCELL HP3",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Text(
                            text = if (capabilities.isRawSupported) "RAW READ" else "JPEG ONLY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Haptic Scrubber Controls
                    ScrubberRow(
                        label = "ISO (${currentIso.toInt()})",
                        value = currentIso,
                        valueRange = 50f..6400f,
                        onValueChange = { newVal ->
                            currentIso = hapticDialState.updateIso(newVal) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    )

                    ScrubberRow(
                        label = "SHUTTER (1/${currentShutter.toInt()}s)",
                        value = currentShutter,
                        valueRange = 1f..8000f,
                        onValueChange = { newVal ->
                            currentShutter = hapticDialState.updateShutter(newVal) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    )

                    ScrubberRow(
                        label = "KELVIN WB (${currentKelvin.toInt()}K)",
                        value = currentKelvin,
                        valueRange = 2000f..10000f,
                        onValueChange = { newVal ->
                            currentKelvin = hapticDialState.updateKelvinWb(newVal) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    )

                    ScrubberRow(
                        label = "FOCUS (${"%.1f".format(currentFocusDiopter)}D)",
                        value = currentFocusDiopter,
                        valueRange = 0f..10f,
                        enabled = capabilities.canOverrideFocusManually(),
                        onValueChange = { newVal ->
                            currentFocusDiopter = hapticDialState.updateFocusDiopter(newVal) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScrubberRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
