package com.kanak.lensora.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class FlightDeckTab(val title: String, val subtitle: String, val icon: ImageVector) {
    RIG("RIG", "HUD & SCRUBBERS", Icons.Default.Camera),
    OPTICS("OPTICS", "SHADERS & RADAR", Icons.Default.Tune),
    NEURAL("NEURAL", "AI DIRECTORS", Icons.Default.Psychology),
    QUANTUM("QUANTUM", "ZSL & WATERMARK", Icons.Default.Shield)
}

/**
 * Material 3 Frosted Glass 4-Tab Navigation Flight-Deck interface.
 */
@Composable
fun FlightDeckNavigationBar(
    selectedTab: FlightDeckTab,
    onTabSelected: (FlightDeckTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .graphicsLayer {
                shape = RoundedCornerShape(18.dp)
                clip = true
            }
            .background(Color(0xFF020617).copy(alpha = 0.88f))
            .padding(vertical = 6.dp, horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlightDeckTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val accentColor = if (isSelected) Color(0xFF00E676) else Color.Gray

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = accentColor
                    )
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = accentColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
