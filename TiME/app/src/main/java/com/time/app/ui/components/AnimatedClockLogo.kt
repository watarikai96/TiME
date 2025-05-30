package com.time.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@Composable
fun AnimatedClockLogo(
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
        ClockFaceCanvas(
            size = size,
            color = color,
            secondColor = secondColor
        )
    }
}

@Composable
private fun ClockFaceCanvas(
    size: Dp,
    color: Color,
    secondColor: Color
) {
    val timeMillis = remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Use frame time for smooth real-time animation
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                timeMillis.longValue = System.currentTimeMillis()
            }
        }
    }

    val now = timeMillis.longValue
    val localTime = LocalTime.now()

    val millis = now % 1000L
    val seconds = localTime.second + (millis / 1000f)
    val minutes = localTime.minute + (seconds / 60f)
    val hours = (localTime.hour % 12) + (minutes / 60f)

    val secondRotation = seconds * 6f
    val minuteRotation = minutes * 6f
    val hourRotation = hours * 30f

    Canvas(modifier = Modifier.size(size)) {
        val canvasSize = size.toPx()
        val scale = canvasSize / 1024f
        val center = Offset(512f * scale, 512f * scale)

        drawCircle(
            color = color,
            radius = 400f * scale,
            center = center,
            style = Stroke(width = 48f * scale)
        )

        fun drawRoundedRectHand(
            x: Float, y: Float, width: Float, height: Float,
            rotation: Float, color: Color, cornerRadius: Float
        ) {
            rotate(rotation, center) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(center.x + x * scale, center.y + y * scale),
                    size = androidx.compose.ui.geometry.Size(width * scale, height * scale),
                    cornerRadius = CornerRadius(cornerRadius * scale)
                )
            }
        }

        drawRoundedRectHand(
            x = -24f, y = -220f, width = 48f, height = 220f,
            rotation = hourRotation, color = color, cornerRadius = 24f
        )

        drawRoundedRectHand(
            x = -24f, y = -300f, width = 48f, height = 300f,
            rotation = minuteRotation, color = color, cornerRadius = 24f
        )

        drawRoundedRectHand(
            x = -4f, y = -360f, width = 8f, height = 360f,
            rotation = secondRotation, color = secondColor, cornerRadius = 4f
        )

        drawCircle(color = secondColor, radius = 12f * scale, center = center)
    }
}

