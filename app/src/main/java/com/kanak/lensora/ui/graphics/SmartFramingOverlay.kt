package com.kanak.lensora.ui.graphics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kanak.lensora.neural.FramingSuggestion

/**
 * Clean Material 3 Day/Night Viewfinder Overlay featuring subtle rule-of-thirds corner guides
 * and a "Recomp AI" action pill button.
 */
@Composable
fun SmartFramingOverlay(
    suggestion: FramingSuggestion,
    modifier: Modifier = Modifier,
    onRecompClick: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Rule of Thirds subtle grid and corner brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val gridColor = Color.White.copy(alpha = 0.20f)
            val strokeWidth = 1.5f

            // Rule-of-Thirds Grid Lines
            val thirdW = w / 3f
            val thirdH = h / 3f

            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            // Vertical Lines
            drawLine(gridColor, Offset(thirdW, 0f), Offset(thirdW, h), strokeWidth, pathEffect = dashEffect)
            drawLine(gridColor, Offset(thirdW * 2, 0f), Offset(thirdW * 2, h), strokeWidth, pathEffect = dashEffect)

            // Horizontal Lines
            drawLine(gridColor, Offset(0f, thirdH), Offset(w, thirdH), strokeWidth, pathEffect = dashEffect)
            drawLine(gridColor, Offset(0f, thirdH * 2), Offset(w, thirdH * 2), strokeWidth, pathEffect = dashEffect)

            // Subtle Corner Framing Brackets
            val cornerLen = 24.dp.toPx()
            val bracketColor = if (suggestion.isBalanced) Color(0xFF10B981) else Color.White.copy(alpha = 0.60f)
            val bracketStroke = 3f

            // Top-Left
            drawLine(bracketColor, Offset(32f, 32f), Offset(32f + cornerLen, 32f), bracketStroke)
            drawLine(bracketColor, Offset(32f, 32f), Offset(32f, 32f + cornerLen), bracketStroke)

            // Top-Right
            drawLine(bracketColor, Offset(w - 32f, 32f), Offset(w - 32f - cornerLen, 32f), bracketStroke)
            drawLine(bracketColor, Offset(w - 32f, 32f), Offset(w - 32f, 32f + cornerLen), bracketStroke)

            // Bottom-Left
            drawLine(bracketColor, Offset(32f, h - 32f), Offset(32f + cornerLen, h - 32f), bracketStroke)
            drawLine(bracketColor, Offset(32f, h - 32f), Offset(32f, h - 32f - cornerLen), bracketStroke)

            // Bottom-Right
            drawLine(bracketColor, Offset(w - 32f, h - 32f), Offset(w - 32f - cornerLen, h - 32f), bracketStroke)
            drawLine(bracketColor, Offset(w - 32f, h - 32f), Offset(w - 32f, h - 32f - cornerLen), bracketStroke)
        }

        // Recomp AI Action Pill
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .clickable { onRecompClick() },
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Crop,
                    contentDescription = "Recomp",
                    tint = if (suggestion.isBalanced) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RECOMP AI • ${suggestion.suggestionText}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
