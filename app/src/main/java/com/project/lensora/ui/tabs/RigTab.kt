package com.project.lensora.ui.tabs

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.lensora.camera.HardwareCapabilitiesInspector
import com.project.lensora.camera.LensCapabilities
import com.project.lensora.ui.camera.CameraPreviewView

/**
 * RIG Tab: Full-screen Camera Viewfinder with 120Hz deferred Compose state read telemetry HUD.
 */
@Composable
fun RigTab(
    modifier: Modifier = Modifier,
    onCapabilitiesUpdated: (LensCapabilities) -> Unit = {}
) {
    var capabilities by remember { mutableStateOf(LensCapabilities()) }

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

        // Telemetry HUD Overlay with Deferred Graphics Layer Rendering (120Hz smooth)
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .fillMaxWidth(0.92f)
                .graphicsLayer {
                    shadowElevation = 8f
                    shape = RoundedCornerShape(12.dp)
                    clip = true
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.75f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Sensor",
                            tint = Color(0xFF00E676)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RIG TELEMETRY • ISOCELL HP3",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                    Text(
                        text = if (capabilities.isRawSupported) "RAW READ" else "JPEG ONLY",
                        color = if (capabilities.isRawSupported) Color(0xFF00E676) else Color(0xFFFFB74D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryItem(
                        label = "ISO RANGE",
                        value = if (capabilities.isoMin != null && capabilities.isoMax != null) {
                            "${capabilities.isoMin} - ${capabilities.isoMax}"
                        } else "ISO 50 - 6400"
                    )
                    TelemetryItem(
                        label = "EXPOSURE (NS)",
                        value = capabilities.formattedExposureTimeRange()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryItem(
                        label = "ACTIVE ARRAY",
                        value = capabilities.activeArraySize?.let { "${it.width()}x${it.height()}" } ?: "200MP ISOCELL"
                    )
                    TelemetryItem(
                        label = "CAMERA ID",
                        value = capabilities.cameraId.ifEmpty { "0 (Back Main)" }
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.LightGray,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )
    }
}
