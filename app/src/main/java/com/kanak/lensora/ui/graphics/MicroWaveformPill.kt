package com.kanak.lensora.ui.graphics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanak.lensora.neural.HistogramResult

/**
 * Collapsible micro-waveform and RGB histogram pill for the viewfinder HUD overlay.
 */
@Composable
fun MicroWaveformPill(
    histogramResult: HistogramResult,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(8.dp)
            .graphicsLayer {
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Equalizer,
                contentDescription = "Waveform",
                tint = Color(0xFF00E676),
                modifier = Modifier.height(18.dp).width(18.dp)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Canvas(
                        modifier = Modifier
                            .width(120.dp)
                            .height(28.dp)
                    ) {
                        val maxCount = (histogramResult.bins.maxOrNull() ?: 1).coerceAtLeast(1)
                        val barWidth = size.width / 256f

                        for (i in 0 until 256 step 4) {
                            val count = histogramResult.bins[i]
                            val barHeight = (count.toFloat() / maxCount) * size.height
                            drawLine(
                                color = Color(0xFF00E676).copy(alpha = 0.85f),
                                start = Offset(i * barWidth, size.height),
                                end = Offset(i * barWidth, size.height - barHeight),
                                strokeWidth = barWidth * 3f
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RGB WAVE",
                        fontSize = 9.sp,
                        color = Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
