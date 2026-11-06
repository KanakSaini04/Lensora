package com.kanak.lensora.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.kanak.lensora.ui.tabs.NeuralTab
import com.kanak.lensora.ui.tabs.OpticsTab
import com.kanak.lensora.ui.tabs.QuantumTab
import com.kanak.lensora.ui.tabs.RigTab
import com.kanak.lensora.ui.theme.LensoraTheme

/**
 * Main Lensora Container Screen featuring the 4-tab Flight-Deck architecture:
 * 1. RIG (Full Camera Stream, Real-Time Telemetry HUD & 2160Hz Tactile Scrubbers)
 * 2. OPTICS (Lens Hardware Inspector, AGSL Shaders & Fixed Focus Safety)
 * 3. NEURAL (AI Directors & Scene Classifier)
 * 4. QUANTUM (ZSL Pre-Capture Buffers & EXIF Hardware Signatures)
 */
@Composable
fun LensoraAppScreen() {
    var selectedTab by remember { mutableStateOf(FlightDeckTab.RIG) }
    var currentCapabilities by remember { mutableStateOf(LensCapabilities()) }

    LensoraTheme {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "LENSORA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PRO",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "200MP ISOCELL HP3",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            },
            bottomBar = {
                FlightDeckNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    FlightDeckTab.RIG -> RigTab(
                        onCapabilitiesUpdated = { cap -> currentCapabilities = cap }
                    )
                    FlightDeckTab.OPTICS -> OpticsTab(capabilities = currentCapabilities)
                    FlightDeckTab.NEURAL -> NeuralTab()
                    FlightDeckTab.QUANTUM -> QuantumTab()
                }
            }
        }
    }
}
