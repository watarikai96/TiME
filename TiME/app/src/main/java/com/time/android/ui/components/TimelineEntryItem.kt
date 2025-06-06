package com.time.android.ui.components

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.twotone.Coffee
import androidx.compose.material.icons.twotone.Pause
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material.icons.twotone.Schedule
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.time.android.model.Category
import com.time.android.model.Subtask
import com.time.android.model.TiME
import com.time.android.ui.icons.IconRepository
import com.time.android.ui.theme.DefaultCategoryColor
import com.time.android.ui.theme.ThemeManager
import com.time.android.viewmodel.TiMEViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineEntryItem(
    entry: TiME,
    categories: List<Category>,
    selectedFormat: TimeFormatOption,
    onToggleCompleted: (TiME) -> Unit,
    onDelete: (String) -> Unit,
    onUpdate: (TiME) -> Unit,
    onTogglePause: (TiME) -> Unit
)
 {

    val safeUpdate: (TiME.() -> TiME) -> Unit = { transform ->
        onUpdate(entry.transform().copy(isPaused = entry.isPaused))
    }

    // Retrieve category color
    val catColorInt = categories.find { it.id == entry.category }?.color?.toInt()
        ?: DefaultCategoryColor.value.toInt()
    val animatedNow = rememberPausedNow(entry.isPaused)
    val categoryColor = Color(catColorInt)
    val now = animatedNow.value
    val duration = (entry.endTime - entry.startTime).coerceAtLeast(1L)
    val pauseOffset = if (entry.isPaused && entry.pauseStartTime != null) {
        now - entry.pauseStartTime
    } else 0L

    val totalPaused = entry.totalPausedDuration + pauseOffset
    val adjustedNow = now - totalPaused

    val elapsed = (adjustedNow - entry.startTime).coerceIn(0L, duration)
    val progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
    val remainingTime = (entry.endTime - adjustedNow).coerceAtLeast(0L)

    val isPaused = entry.isPaused
    val isRunning = now in entry.startTime..entry.endTime && !entry.isCompleted && !isPaused
    val isUpcoming = now < entry.startTime && !isPaused

    LaunchedEffect(now) {
        val pauseOffset = if (entry.isPaused && entry.pauseStartTime != null) {
            now - entry.pauseStartTime
        } else 0L
        val totalPaused = entry.totalPausedDuration + pauseOffset
        val adjustedNow = now - totalPaused

        if (adjustedNow > entry.endTime && !entry.isCompleted) {
            onToggleCompleted(entry.copy(isCompleted = true))
        }
    }

     // Calculate progress before start
    val preStartProgress = ((now - entry.createdAt).toFloat() /
            (entry.startTime - entry.createdAt).coerceAtLeast(1L)).coerceIn(0f, 1f)

    // Animation for filling progress bar
    val fillAnim by animateFloatAsState(
        targetValue = when {
            isRunning || entry.isPaused -> progress
            isUpcoming -> preStartProgress
            else -> 1f
        },
        animationSpec = tween(durationMillis = 600),
        label = "fillAnim"
    )


    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "arrowRotate")
    var editableStart by remember { mutableLongStateOf(entry.startTime) }
    var editableEnd by remember { mutableLongStateOf(entry.endTime) }
    var editableNotes by remember { mutableStateOf(entry.notes) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startFormatted =
        Instant.ofEpochMilli(editableStart).atZone(ZoneId.systemDefault()).format(timeFormatter)
    val endFormatted =
        Instant.ofEpochMilli(editableEnd).atZone(ZoneId.systemDefault()).format(timeFormatter)
    val icon = IconRepository.getIconByName(entry.iconName)

     val subtasks = remember { mutableStateListOf<Subtask>() }
     LaunchedEffect(entry.id) {
         subtasks.clear()
         subtasks.addAll(entry.subtasks)
     }

     var newSubtask by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionDateFormatted = Instant.ofEpochMilli(entry.startTime)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
    val elapsedTime = calculateElapsedTime(entry, animatedNow.value)

    // Realtime Break Timer
    var breakSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(entry.id, entry.isPaused) {
        if (entry.isPaused) {
            while (true) {
                val pauseStart = entry.pauseStartTime ?: System.currentTimeMillis()
                breakSeconds = ((System.currentTimeMillis() - pauseStart) / 1000).toInt()
                delay(1000L)
            }
        } else {
            breakSeconds = 0
        }
    }
    val pausedOverlayAlpha by animateFloatAsState(
        targetValue = if (entry.isPaused) 0.25f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "PausedOverlayAlpha"
    )

    val pausedOverlayColor = Color.Red.copy(alpha = pausedOverlayAlpha)


    fun showTimePicker(isStart: Boolean) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = if (isStart) editableStart else editableEnd
        }
        TimePickerDialog(
            context,
            { _, h, m ->
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, m)
                val newMillis = cal.timeInMillis
                if (isStart && newMillis < editableEnd) {
                    editableStart = newMillis
                    safeUpdate { copy(startTime = newMillis) }
                } else if (!isStart && newMillis > editableStart) {
                    editableEnd = newMillis
                    safeUpdate { copy(endTime = newMillis) }
                }
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun Color.darken(factor: Float = 0.8f): Color {
        return Color(
            red = (red * factor).coerceIn(0f, 1f),
            green = (green * factor).coerceIn(0f, 1f),
            blue = (blue * factor).coerceIn(0f, 1f),
            alpha = alpha
        )
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


// Break, Pause, Elapsed, Running Counters

    //COUNTER LOGICS

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(categoryColor.copy(alpha = 0.06f))
            .clickable { expanded = !expanded }
            .animateContentSize()
    )
    {



        if (!entry.isCompleted) {
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
                            entry.isPaused -> pausedOverlayColor
                            isUpcoming -> Color.Gray.copy(alpha = 0.15f)
                            else -> categoryColor.copy(alpha = 0.25f)
                        }
                    )
            )
        }

        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = entry.isCompleted,
                        onCheckedChange = { onToggleCompleted(entry.copy(isCompleted = it)) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = categoryColor,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(Modifier.width(6.dp))

                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = entry.iconName,
                            tint = categoryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        IconRepository.RenderIcon(
                            name = entry.iconName,
                            color = categoryColor,
                            fontSizeSp = 20
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (entry.isCompleted) TextDecoration.LineThrough else null
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(arrowRotation),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Animated session state chips
            if (!entry.isCompleted && (entry.isPaused || isRunning || isUpcoming)) {
                val breakTimeFormatted by produceState(
                    initialValue = "00:00",
                    key1 = entry.id,
                    key2 = entry.isPaused
                ) {
                    if (entry.isPaused) {
                        while (true) {
                            val now = System.currentTimeMillis()
                            val pauseStart = entry.pauseStartTime ?: now
                            val elapsed =
                                ((now - pauseStart) / 1000).toInt()  // Convert milliseconds to seconds
                            val mins = elapsed / 60  // Convert seconds to minutes
                            val secs = elapsed % 60  // Get remaining seconds
                            value = String.format("%02d:%02d", mins, secs)
                            delay(1000L)  // Wait for a second before updating again
                        }
                    } else {
                        value = "00:00"  // Reset break time when not paused
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {

                    when {
                        entry.isPaused -> {
                            InfoChip(
                                icon = Icons.TwoTone.Pause,
                                text = "Paused",
                                bgColor = Color(0xFFE57373),
                                iconTint = Color.White,
                                textColor = Color.White
                            )



                            InfoChip(
                                icon = Icons.TwoTone.Coffee,
                                text = "Break: $breakTimeFormatted",
                                bgColor = ThemeManager.accentColor(),
                                iconTint = Color.White,
                                textColor = Color.White
                            )


                        }

                        isRunning -> {
                            InfoChip(
                                icon = Icons.TwoTone.Timer,
                                text = "${formatDuration(remainingTime, selectedFormat)} remaining",
                                bgColor = categoryColor,
                                iconTint = Color.White,
                                textColor = Color.White
                            )
                        }

                        isUpcoming -> {
                            InfoChip(
                                icon = Icons.TwoTone.Schedule,
                                text = when {
                                    entry.isPaused -> "Paused"
                                    entry.isRunning() -> "Running: ${formatDuration(elapsedTime, selectedFormat)}"
                                    (entry.startTime - animatedNow.value) / 60000 < 1 -> "Starts in ${((entry.startTime - animatedNow.value) / 1000)} sec"
                                    (entry.startTime - animatedNow.value) / 60000 < 60 -> "Starts in ${((entry.startTime - animatedNow.value) / 60000)} min"
                                    else -> "Starts in ${((entry.startTime - animatedNow.value) / 60000) / 60}h ${((entry.startTime - animatedNow.value) / 60000) % 60}m"
                                },
                                bgColor = categoryColor,
                                iconTint = Color.White,
                                textColor = Color.White
                            )
                        }
                    }

                    if (entry.isPaused || isRunning) {
                        InfoChip(
                            icon = Icons.TwoTone.Schedule,
                            text = "${formatDuration(elapsedTime, selectedFormat)} elapsed",
                            bgColor = ThemeManager.accentColor().darken(0.85f),
                            iconTint = Color.White,
                            textColor = Color.White
                        )
                    }
                }
            }

            // Add Focused Time and Break Time badges for completed tasks
            if (entry.isCompleted) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    val focusedMillis = entry.endTime - entry.startTime - entry.totalPausedDuration

                    InfoChip(
                        icon = Icons.TwoTone.Timer,
                        text = "Focused Time: ${formatDuration(focusedMillis, selectedFormat)}",
                        bgColor = categoryColor,
                        iconTint = Color.White,
                        textColor = Color.White
                    )

                    InfoChip(
                        icon = Icons.TwoTone.Coffee,
                        text = "Break Time: ${formatDuration((entry.breakTimeTotal ?: 0L) * 60_000L, selectedFormat)}",
                        bgColor = ThemeManager.accentColor(),
                        iconTint = Color.White,
                        textColor = Color.White
                    )
                }
            }
        }
    }

    //Drop Down Items

    AnimatedVisibility(expanded, enter = expandVertically(), exit = shrinkVertically()) {
        Column {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showTimePicker(true) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ThemeManager.buttonColors(),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(
                        Icons.TwoTone.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Start: $startFormatted",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = { showTimePicker(false) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ThemeManager.buttonColors(),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(
                        Icons.TwoTone.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "End: $endFormatted",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            //Tag Chips

            if (entry.tags.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isDark = isSystemInDarkTheme()
                    entry.tags.forEach { tag ->
                        val background = categoryColor.copy(alpha = if (isDark) 0.18f else 0.12f)
                        val border = categoryColor.copy(alpha = 0.3f)
                        val textColor = if (isDark) categoryColor else categoryColor.darken(0.7f)

                        Box(
                            modifier = Modifier
                                .background(background, shape = RoundedCornerShape(50))
                                .border(1.dp, border, shape = RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = textColor,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Notes",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            TextField(
                value = editableNotes,
                onValueChange = {
                    editableNotes = it
                    safeUpdate { copy(notes = it) }
                },
                placeholder = {
                    Text(
                        "Write a note...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )


            //Subtasks

            if (subtasks.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Subtasks",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    subtasks.forEachIndexed { i, sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = sub.isDone,
                                onCheckedChange = {
                                    subtasks[i] = sub.copy(isDone = it)
                                    safeUpdate { copy(subtasks = subtasks.toList()) }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = categoryColor,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Text(
                                text = sub.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = if (sub.isDone) TextDecoration.LineThrough else null
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            if (sub.isDone) {
                                IconButton(
                                    onClick = {
                                        subtasks.removeAt(i)
                                        safeUpdate { copy(subtasks = subtasks) }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TextField for adding new subtasks
            Spacer(Modifier.height(12.dp))
            TextField(
                value = newSubtask,
                onValueChange = { newSubtask = it },
                placeholder = {
                    Text(
                        "Add a subtask...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newSubtask.isNotBlank()) {
                                subtasks.add(Subtask(newSubtask))
                                newSubtask = ""
                                safeUpdate { copy(subtasks = subtasks) }
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                    .padding(horizontal = 2.dp), // gives better alignment of inner content
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            //Date Stamp
            Spacer(Modifier.height(10.dp))
            Text(
                text = sessionDateFormatted,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.3.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )

            //Date Picker + Duplicate + Delete + Pause
            Spacer(Modifier.height(12.dp))

            Column {
                val viewModel: TiMEViewModel = viewModel()
                var showDuplicateDialog by remember { mutableStateOf(false) }
                val datePickerState =
                    rememberDatePickerState(initialSelectedDateMillis = entry.startTime)

                // Button Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Pause/Resume Button
                    if (!entry.isCompleted) {
                        Button(
                            onClick = {
                                val now = System.currentTimeMillis()

                                // Adjusting the pause/resume logic
                                val toggled = if (!entry.isPaused) {
                                    entry.copy(isPaused = true, pauseStartTime = now)
                                } else {
                                    val pauseStarted = entry.pauseStartTime ?: now
                                    val pausedDuration = now - pauseStarted
                                    val newTotalPaused = entry.totalPausedDuration + pausedDuration
                                    val originalDuration = entry.endTime - entry.startTime

                                    val adjustedEndTime = if (now > entry.endTime) {
                                        // Adjust the end time to compensate for pause overflow
                                        now + (originalDuration - calculateElapsedTime(
                                            entry.copy(totalPausedDuration = newTotalPaused), now
                                        )).coerceAtLeast(0L)
                                    } else entry.endTime

                                    // Updating entry state with new values
                                    entry.copy(
                                        isPaused = false,
                                        pauseStartTime = null,
                                        totalPausedDuration = newTotalPaused,
                                        endTime = adjustedEndTime,
                                        breakTimeTotal = (entry.breakTimeTotal
                                            ?: 0L) + (pausedDuration / 60000)
                                    )
                                }

                                onTogglePause(toggled)
                            },
                            colors = ThemeManager.buttonColors(),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(
                                imageVector = if (entry.isPaused) Icons.TwoTone.PlayArrow else Icons.TwoTone.Pause,
                                contentDescription = if (entry.isPaused) "Resume" else "Pause",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (entry.isPaused) "Resume" else "Pause",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }


                    // Duplicate Button
                    Button(
                        onClick = { showDuplicateDialog = true },
                        colors = ThemeManager.buttonColors(),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Duplicate",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Duplicate", style = MaterialTheme.typography.labelLarge)
                    }

                    // Delete Button
                    Button(
                        onClick = { onDelete(entry.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Delete", style = MaterialTheme.typography.labelLarge)
                    }
                }

                    // Duplicate Picker Modal
                if (showDuplicateDialog) {
                    DatePickerDialog(
                        onDismissRequest = { showDuplicateDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val selectedDateMillis = datePickerState.selectedDateMillis
                                if (selectedDateMillis != null) {
                                    val originalCal = Calendar.getInstance()
                                        .apply { timeInMillis = entry.startTime }
                                    val newStartCal = Calendar.getInstance().apply {
                                        timeInMillis = selectedDateMillis
                                        set(
                                            Calendar.HOUR_OF_DAY,
                                            originalCal.get(Calendar.HOUR_OF_DAY)
                                        )
                                        set(Calendar.MINUTE, originalCal.get(Calendar.MINUTE))
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }

                                    val newStart = newStartCal.timeInMillis
                                    val duration = entry.endTime - entry.startTime
                                    val newEnd = newStart + duration

                                    val duplicated = entry.copy(
                                        id = UUID.randomUUID().toString(),
                                        createdAt = System.currentTimeMillis(),
                                        title = entry.title,
                                        startTime = newStart,
                                        endTime = newEnd,
                                        isCompleted = false,
                                        isPaused = false,
                                        pauseStartTime = null,
                                        totalPausedDuration = 0L
                                    )

                                    viewModel.add(duplicated)
                                    showDuplicateDialog = false
                                }
                            }) {
                                Text("Confirm", style = MaterialTheme.typography.bodyMedium)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDuplicateDialog = false }) {
                                Text("Cancel", style = MaterialTheme.typography.bodyMedium)
                            }


                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                }

            }
    }
}

fun calculateElapsedTime(session: TiME, now: Long): Long {
    val pauseOffset = if (session.isPaused) now - (session.pauseStartTime ?: now) else 0L
    return (now - session.startTime - session.totalPausedDuration - pauseOffset).coerceAtLeast(0L)
}

@Composable
fun rememberPausedNow(isPaused: Boolean): State<Long> {
    val now = remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(isPaused) {
        while (!isPaused) {
            now.longValue = System.currentTimeMillis()
            delay(1000L)
        }
    }

    return now
}


