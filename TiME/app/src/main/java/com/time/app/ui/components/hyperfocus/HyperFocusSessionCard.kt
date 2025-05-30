package com.time.app.ui.components.hyperfocus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Cancel
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.Coffee
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.Pause
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material.icons.twotone.Schedule
import androidx.compose.material.icons.twotone.Stop
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.time.app.model.ExecutableSession
import com.time.app.ui.icons.IconRepository
import com.time.app.ui.theme.ThemeManager
import com.time.app.viewmodel.HyperFocusViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun formatTime(minutes: Int): String {
    return when {
        minutes >= 1440 -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
        minutes >= 60 -> "${minutes / 60}h ${minutes % 60}m"
        minutes <= 0 -> "0m"
        else -> "${minutes}m"
    }
}

fun formatLiveTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = (totalSeconds / 60).coerceAtLeast(0)
    val seconds = (totalSeconds % 60).coerceAtLeast(0)
    return "${minutes}m ${seconds}s"
}

// EXPERIMENTAL FEATURE

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HyperFocusSessionCard(
    viewModel: HyperFocusViewModel,
    session: ExecutableSession,
    sessionIndex: Int,
    sessionNumber: Int?,
    currentSessionIndex: Int,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onEnd: () -> Unit,
    onDelete: () -> Unit,
    originalStart: Long,
    type: CarouselType,
    onDurationChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {


    val now = rememberUpdatedState(System.currentTimeMillis())



    val isActive by remember {
        derivedStateOf { sessionIndex == currentSessionIndex }
    }
    val isUpcoming = sessionIndex > currentSessionIndex
    val isBreak = session.isBreak
    var breakSeconds by remember { mutableIntStateOf(0) }

    val iconName = session.iconName ?: "Timer"
    val categoryColor = if (isBreak) Color(0xFFE0E0E0) else Color((session.categoryColor ?: 0xFF888888).toInt())
    val isCompleted = session.isCompleted
    var isCancelled = session.isCancelled

    var isExpanded by remember { mutableStateOf(false) }
    val durationMs = session.duration * 60_000L

// Calculate focus time accounting for break windows
    val breakMillis = session.breakWindows.sumOf { it.end - it.start }
    val focusElapsedMs = (now.value - session.scheduledStart - breakMillis).coerceIn(0, durationMs)


    // Modify the elapsed time calculation:
    val elapsed = when {
        isActive -> focusElapsedMs
        isUpcoming -> 0L
        isCompleted || isCancelled -> (session.actualDuration ?: session.duration) * 60_000L
        else -> 0L
    }

    val remaining = (durationMs - elapsed).coerceAtLeast(0L)




    // Timer logic

    LaunchedEffect(isActive, session.isCompleted, session.isCancelled) {
    while (isActive && !session.isCompleted && !session.isCancelled) {
            if (isPaused) {
                val pauseStart = session.pauseStartTime ?: now.value
                breakSeconds = ((now.value - pauseStart) / 1000).toInt()
            } else {
                breakSeconds = 0
            }

            val elapsed = (now.value - session.scheduledStart).coerceIn(0, session.duration * 60000L)
            val progressFraction = (elapsed.toFloat() / (session.duration * 60000L)).coerceIn(0f, 1f)

            if (progressFraction >= 1f && !session.isCompleted && !session.isCancelled) {
                viewModel.markSessionAsCompleted(session.id)
                break
            }

            delay(1000L)
        }
    }



    // Animation states
    val currentTime by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1000)
        }
    }


    val progress by remember {
        derivedStateOf {
            val elapsed = (currentTime - session.scheduledStart).coerceIn(0, durationMs)
            (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)
        }
    }



    val preStartProgress = ((currentTime - originalStart).toFloat() /
            (session.scheduledStart - originalStart).coerceAtLeast(1L)).coerceIn(0f, 1f)

    //Progress Animation
    val fillAnim by animateFloatAsState(
        targetValue = when {
            session.isCompleted || session.isCancelled -> 1f
            isActive && !isPaused -> progress
            isUpcoming -> preStartProgress
            else -> 0f
        },
        animationSpec = tween(600),
        label = "fillAnim"
    )



    val pausedOverlayAlpha by animateFloatAsState(
        targetValue = if (isPaused && isActive) 0.25f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "PausedAlpha"
    )

    Card(

        modifier = modifier
            .padding(vertical = 6.dp, horizontal = 2.dp)
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp, when {
                isCancelled -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                isActive -> categoryColor
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        )
    )

    {

        Box(modifier = Modifier.fillMaxSize()) {
            // Animated fill layer (scaleX with transformOrigin)
            if (!isCompleted && !isCancelled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            scaleX = fillAnim
                            transformOrigin = TransformOrigin(0f, 0f)
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when {
                                isPaused && isActive -> Color.Red.copy(alpha = pausedOverlayAlpha)
                                isUpcoming -> Color.Gray.copy(alpha = 0.15f)
                                else -> categoryColor.copy(alpha = 0.25f)
                            }
                        )
                )
            }

            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isBreak && sessionNumber != null) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(categoryColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$sessionNumber",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = categoryColor
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                            }

                            Text(
                                session.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    textDecoration = if (isCancelled) TextDecoration.LineThrough else null
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        // Enhanced status chips with break timer
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            when {
                                isActive && isPaused -> {
                                    InfoChip(
                                        icon = Icons.TwoTone.Pause,
                                        text = "Paused",
                                        bgColor = Color(0xFFE57373),
                                        iconTint = Color.White,
                                        textColor = Color.White
                                    )
                                    InfoChip(
                                        icon = Icons.TwoTone.Coffee,
                                        text = "Break: ${formatBreakTime(breakSeconds)}",
                                        bgColor = ThemeManager.accentColor(),
                                        iconTint = Color.White,
                                        textColor = Color.White
                                    )
                                }

                                isActive -> {
                                    InfoChip(
                                        icon = Icons.TwoTone.Timer,
                                        text = "${formatLiveTime(remaining)} remaining",
                                        bgColor = categoryColor,
                                        iconTint = Color.White,
                                        textColor = Color.White
                                    )
                                    InfoChip(
                                        icon = Icons.TwoTone.Schedule,
                                        text = "${formatLiveTime(elapsed)} elapsed",
                                        bgColor = categoryColor.copy(alpha = 0.1f),
                                        iconTint = categoryColor,
                                        textColor = categoryColor
                                    )
                                }

                                isUpcoming -> {
                                    val minsUntilStart =
                                        ((session.scheduledStart - now.value) / 60000).toInt()
                                            .coerceAtLeast(0)
                                    InfoChip(
                                        icon = Icons.TwoTone.Schedule,
                                        text = "Starts in ${formatTime(minsUntilStart)}",
                                        bgColor = categoryColor.copy(alpha = 0.1f),
                                        iconTint = categoryColor,
                                        textColor = categoryColor
                                    )
                                }

                                isCompleted || isCancelled -> {
                                    val focusMin = ((session.actualDuration
                                        ?: session.duration) - session.breakDuration).coerceAtLeast(0)
                                    val breakMin = session.breakDuration

                                    if (!isBreak) {
                                        InfoChip(
                                            icon = if (isCompleted) Icons.TwoTone.CheckCircle else Icons.TwoTone.Cancel,
                                            text = if (isCompleted) "Completed" else "Cancelled",
                                            bgColor = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                            iconTint = Color.White,
                                            textColor = Color.White
                                        )
                                        InfoChip(
                                            icon = Icons.TwoTone.Schedule,
                                            text = "Focus: ${formatTime(focusMin)}",
                                            bgColor = categoryColor,
                                            iconTint = Color.White,
                                            textColor = Color.White
                                        )
                                        if (breakMin > 0) {
                                            InfoChip(
                                                icon = Icons.TwoTone.Coffee,
                                                text = "Break: ${formatTime(breakMin)}",
                                                bgColor = ThemeManager.accentColor(),
                                                iconTint = Color.White,
                                                textColor = Color.White
                                            )
                                        }
                                    } else {
                                        InfoChip(
                                            icon = Icons.TwoTone.Coffee,
                                            text = "Break: ${formatTime(session.actualDuration ?: session.duration)}",
                                            bgColor = ThemeManager.accentColor(),
                                            iconTint = Color.White,
                                            textColor = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    IconRepository.getIconByName(iconName)?.let {
                        Icon(
                            it,
                            contentDescription = iconName,
                            tint = categoryColor,
                            modifier = Modifier.size(26.dp)
                        )
                    } ?: IconRepository.RenderIcon(name = iconName, color = categoryColor)
                }

                // Expanded content with actions
                AnimatedVisibility(visible = isExpanded && !isCompleted && !isCancelled) {
                    Column(
                        Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DurationAdjuster(session.duration, onDurationChange, categoryColor)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (isActive) {
                                if (isPaused) {
                                    Button(
                                        onClick = onResume,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = categoryColor)
                                    ) {
                                        Icon(Icons.TwoTone.PlayArrow, null)
                                        Spacer(Modifier.width(4.dp))
                                        Text("Resume")
                                    }
                                } else {
                                    Button(
                                        onClick = onPause,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = categoryColor)
                                    ) {
                                        Icon(Icons.TwoTone.Pause, null)
                                        Spacer(Modifier.width(4.dp))
                                        Text("Pause")
                                    }
                                }
                            }

                            //END BUTTON

                            OutlinedButton(
                                onClick = {
                                    if (!session.isCompleted && !session.isCancelled) {
                                        viewModel.endSession(manual = true)
                                    }
                                    onEnd()
                                },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, categoryColor)
                            ) {
                                Icon(Icons.TwoTone.Stop, null)
                                Spacer(Modifier.width(4.dp))
                                Text("End")
                            }

                            if (!isActive) {
                                IconButton(onClick = onDelete) {
                                    Icon(
                                        Icons.TwoTone.Delete,
                                        null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                // Expand/collapse button
                AnimatedVisibility(
                    visible = !isCompleted && !isCancelled,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded }
                    ) {
                        Icon(
                            Icons.TwoTone.KeyboardArrowDown,
                            null,
                            Modifier.rotate(if (isExpanded) 180f else 0f),
                            tint = categoryColor
                        )
                    }
                }
            }
        }
        }
    }


//HELPER FUNCTIONS

private fun formatBreakTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}


@Composable
fun InfoChip(
    icon: ImageVector,
    text: String,
    bgColor: Color = ThemeManager.accentColor(),
    iconTint: Color = MaterialTheme.colorScheme.onPrimary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .heightIn(min = 32.dp)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun DurationAdjuster(
    currentDuration: Int,
    onDurationChange: (Int) -> Unit,
    color: Color
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempDuration by remember { mutableIntStateOf(currentDuration) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Adjust Duration") },
            text = {
                Column {
                    Slider(
                        value = tempDuration.toFloat(),
                        onValueChange = { value ->
                            tempDuration = (value / 5).roundToInt() * 5  // Ensure proper rounding
                        },
                        valueRange = 5f..120f,
                        steps = 23,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = "$tempDuration minutes",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = color
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDurationChange(tempDuration)
                        showDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    OutlinedButton(
        onClick = { showDialog = true },
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.TwoTone.Schedule, "Duration")
        Spacer(Modifier.width(8.dp))
        Text("Adjust Duration (${currentDuration}m)")
    }
}
