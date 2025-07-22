package com.time.android.ui.components.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnimatedClockLoader(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    secondColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Clock Loader Canvas
        ClockFaceCanvas(
            size = size,
            color = color,
            secondColor = secondColor
        )

        // TiME Title
        Text(
            text = "TiME",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = color
            )
        )
    }
}

@Composable
private fun ClockFaceCanvas(
    size: Dp,
    color: Color,
    secondColor: Color
) {
    var secondRotation by remember { mutableFloatStateOf(0f) }
    var minuteRotation by remember { mutableFloatStateOf(0f) }
    var hourRotation by remember { mutableFloatStateOf(0f) }

    // Continuous loader-style rotation
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            secondRotation = (secondRotation + 6f) % 360f
            minuteRotation = (minuteRotation + 2f) % 360f
            hourRotation = (hourRotation + 1f) % 360f
        }
    }

    Canvas(modifier = Modifier.size(size)) {
        val canvasSize = size.toPx()
        val scale = canvasSize / 1024f
        val center = Offset(512f * scale, 512f * scale)

        // Outer Ring from SVG
        drawCircle(
            color = color,
            radius = 400f * scale,
            center = center,
            style = Stroke(width = 48f * scale)
        )

        // Hand-drawing helper
        fun DrawScope.drawRoundedRectHand(
            x: Float,
            y: Float,
            width: Float,
            height: Float,
            rotation: Float,
            color: Color,
            cornerRadius: Float
        ) {
            rotate(rotation, center) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(center.x + x * scale, center.y + y * scale),
                    size = Size(width * scale, height * scale),
                    cornerRadius = CornerRadius(cornerRadius * scale, cornerRadius * scale)
                )
            }
        }

        // Hour Hand
        drawRoundedRectHand(
            x = -24f, y = -220f,
            width = 48f, height = 220f,
            rotation = hourRotation,
            color = color,
            cornerRadius = 24f
        )

        // Minute Hand
        drawRoundedRectHand(
            x = -24f, y = -300f,
            width = 48f, height = 300f,
            rotation = minuteRotation,
            color = color,
            cornerRadius = 24f
        )

        // Second Hand
        drawRoundedRectHand(
            x = -4f, y = -360f,
            width = 8f, height = 360f,
            rotation = secondRotation,
            color = secondColor,
            cornerRadius = 4f
        )

        // Center Dot
        drawCircle(color = secondColor, radius = 12f * scale, center = center)
    }
}
