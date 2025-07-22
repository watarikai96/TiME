package com.time.android.ui.components.timeline

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun FocusStreakBadge(streak: Int, modifier: Modifier = Modifier) {
    if (streak < 2) return

    var animatedStreak by remember { mutableIntStateOf(0) }
    var showFlame by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "flamePulse")

    //  Subtle shine: animate between soft and bright color
    val flameTint by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        targetValue = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameColor"
    )

    val finalTint = if (showFlame) flameTint else MaterialTheme.colorScheme.onSurfaceVariant

    // Count-up effect
    LaunchedEffect(streak) {
        (1..streak).forEach {
            animatedStreak = it
            delay(100)
        }
        delay(400)
        showFlame = true
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.TwoTone.LocalFireDepartment,
                contentDescription = "Focus Streak",
                tint = finalTint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = "$animatedStreak-Day Focus Streak",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}



