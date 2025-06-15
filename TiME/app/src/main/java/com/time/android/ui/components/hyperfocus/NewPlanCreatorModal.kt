package com.time.android.ui.components.hyperfocus

import androidx.compose.foundation.background
import com.time.android.viewmodel.HyperFocusViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AirlineSeatReclineNormal
import androidx.compose.material.icons.twotone.Hotel
import androidx.compose.material.icons.twotone.Repeat
import androidx.compose.material.icons.twotone.RepeatOne
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.time.android.model.Category
import com.time.android.ui.components.reusables.CategoryIconSelector
import com.time.android.ui.components.reusables.FrequencyInputDialog
import com.time.android.ui.theme.QuietCraftTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlanCreatorModal(
    viewModel: HyperFocusViewModel,
    categories: List<Category>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Date handling - sessions can only be scheduled within next 24 hours
    val now = System.currentTimeMillis()
    val maxDate = now + 24 * 60 * 60 * 1000 // 24 hours from now

    // Initialize session times based on count
    val perSessionStarts = remember { mutableStateListOf<Long>() }
    val perSessionEnds = remember { mutableStateListOf<Long>() }

    // Track initialization state
    var isInitialized by remember { mutableStateOf(false) }

    // Initialize lists when count changes or mode changes
    LaunchedEffect(viewModel.sessionCount.intValue, viewModel.isGlobalMode.value) {
        val count = viewModel.sessionCount.intValue

        // Initialize individual arrays if needed
        if (viewModel.individualTitles.size < count) {
            viewModel.individualTitles.addAll(List(count - viewModel.individualTitles.size) { "" })
        }
        if (viewModel.individualDurations.size < count) {
            viewModel.individualDurations.addAll(List(count - viewModel.individualDurations.size) { 25 })
        }
        if (viewModel.individualCategories.size < count) {
            viewModel.individualCategories.addAll(List(count - viewModel.individualCategories.size) { null })
        }
        if (viewModel.individualIcons.size < count) {
            viewModel.individualIcons.addAll(List(count - viewModel.individualIcons.size) { "DefaultIcon" })
        }

        // Initialize individual titles/durations if in individual mode
        if (!viewModel.isGlobalMode.value) {
            while (viewModel.individualTitles.size < count) {
                viewModel.individualTitles.add("")
            }
            while (viewModel.individualDurations.size < count) {
                viewModel.individualDurations.add(25)
            }
            while (viewModel.individualCategories.size < count) {
                viewModel.individualCategories.add(null)
            }
            while (viewModel.individualIcons.size < count) {
                viewModel.individualIcons.add("DefaultIcon")
            }
        }

        // Initialize session times
        perSessionStarts.clear()
        perSessionEnds.clear()
        var currentStart = now
        repeat(count) { index ->
            val duration = if (viewModel.isGlobalMode.value) {
                viewModel.globalSessionDuration.intValue.minutesToMillis()
            } else {
                viewModel.individualDurations[index].minutesToMillis()
            }
            val end = currentStart + duration
            perSessionStarts.add(currentStart)
            perSessionEnds.add(end)
            currentStart = end
        }

        isInitialized = true
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxHeight() // Don't use 0.9f
            .background(QuietCraftTheme.cardBackground), // Force a solid background
        containerColor = QuietCraftTheme.cardBackground,
        tonalElevation = 0.dp // Prevent shadow overlays
    )
    {
        if (!isInitialized) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@ModalBottomSheet
        }

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 24.dp), // Control your own padding
            verticalArrangement = Arrangement.spacedBy(QuietCraftTheme.Spacing.large)
        )

        {
            // Header
            Text("New HyperFocus Plan", style = QuietCraftTheme.headlineStyle)

            // Plan Name (required)
            QuietCraftTheme.QuietCraftTextField(
                value = viewModel.sessionPlanName.value,
                onValueChange = { viewModel.sessionPlanName.value = it },
                placeholder = "Plan Name",
                isError = viewModel.sessionPlanName.value.isBlank()
            )

            // Number of Sessions (1-10)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QuietCraftTheme.QuietCraftLabel("Number of Sessions (${viewModel.sessionCount.intValue})")
                Slider(
                    value = viewModel.sessionCount.intValue.toFloat(),
                    onValueChange = { viewModel.sessionCount.intValue = it.roundToInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )
            }

            // Global vs Individual Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Use Individual Session Settings", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = !viewModel.isGlobalMode.value,
                    onCheckedChange = {
                        viewModel.isGlobalMode.value = !it
                        if (!it) { // When switching to individual mode
                            viewModel.individualTitles.fill("")
                            viewModel.individualDurations.fill(25)
                        }
                    }
                )
            }

            if (viewModel.isGlobalMode.value) {
                // GLOBAL SETTINGS
                QuietCraftTheme.QuietCraftTextField(
                    value = viewModel.globalSessionTitle.value,
                    onValueChange = { viewModel.globalSessionTitle.value = it },
                    placeholder = "Session Title",
                    isError = viewModel.globalSessionTitle.value.isBlank()
                )

                // Global duration picker - use first session for global settings
                if (perSessionStarts.isNotEmpty() && perSessionEnds.isNotEmpty()) {
                    SessionTimePicker(
                        label = "Session Duration",
                        startTime = perSessionStarts[0],
                        endTime = perSessionEnds[0],
                        maxDate = maxDate,
                        onTimeSelected = { start, end ->
                            val duration = end - start
                            viewModel.globalSessionDuration.intValue = duration.toMinutes().toInt()

                            // Update all sessions to use this duration
                            perSessionStarts.indices.forEach { index ->
                                val newStart = if (index == 0) start else perSessionEnds[index - 1]
                                val newEnd = newStart + duration
                                perSessionStarts[index] = newStart
                                perSessionEnds[index] = newEnd
                            }
                        }
                    )
                }


                // Global category selector
                Text("Session", style = QuietCraftTheme.subtleLabelStyle)
                CategoryIconSelector(
                    categories = categories,
                    selectedCategory = viewModel.selectedCategory.value,
                    selectedIconName = viewModel.selectedIconName.value,
                    onCategorySelected = { viewModel.selectedCategory.value = it },
                    onIconSelected = { viewModel.selectedIconName.value = it }
                )
            } else {
                // INDIVIDUAL SETTINGS
                repeat(viewModel.sessionCount.intValue) { index ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        // Session title
                        QuietCraftTheme.QuietCraftTextField(
                            value = viewModel.individualTitles.getOrElse(index) { "" },
                            onValueChange = {
                                viewModel.individualTitles[index] = it
                            },
                            placeholder = "Title for Session ${index + 1}",
                            isError = viewModel.individualTitles.getOrNull(index).isNullOrBlank()
                        )

                        // Individual duration picker
                        SessionTimePicker(
                            label = "Session ${index + 1} Duration",
                            startTime = perSessionStarts.getOrElse(index) { now },
                            endTime = perSessionEnds.getOrElse(index) { now + 25.minutesToMillis() },
                            maxDate = maxDate,
                            onTimeSelected = { start, end ->
                                // Update this session's times
                                if (index < perSessionStarts.size && index < perSessionEnds.size) {
                                    perSessionStarts[index] = start
                                    perSessionEnds[index] = end
                                    viewModel.individualDurations[index] = (end - start).toMinutes().toInt()

                                    // Update subsequent sessions' start times
                                    for (i in index + 1 until perSessionStarts.size) {
                                        val prevEnd = perSessionEnds[i - 1]
                                        val duration = perSessionEnds[i] - perSessionStarts[i]
                                        perSessionStarts[i] = prevEnd
                                        perSessionEnds[i] = prevEnd + duration
                                    }
                                }
                            }
                        )

                        // Individual category and icon selector
                        Text(
                            "Session ${index + 1}",
                            style = QuietCraftTheme.subtleLabelStyle,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        CategoryIconSelector(
                            categories = categories,
                            selectedCategory = viewModel.individualCategories.getOrNull(index),
                            selectedIconName = viewModel.individualIcons.getOrElse(index) { "DefaultIcon" },
                            onCategorySelected = {
                                if (index < viewModel.individualCategories.size) {
                                    viewModel.individualCategories[index] = it
                                }
                            },
                            onIconSelected = {
                                if (index < viewModel.individualIcons.size) {
                                    viewModel.individualIcons[index] = it
                                }
                            }
                        )
                    }
                }
            }

            // Break Settings Section
            BreakSettingsSection(viewModel)

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                QuietCraftTheme.QuietCraftTextButton(
                    label = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )


                //GLOBAL AND INDIVIDUAL FORM VALIDATOR

                val isFormValid = viewModel.sessionPlanName.value.isNotBlank() && (
                        if (viewModel.isGlobalMode.value) {
                            viewModel.globalSessionTitle.value.isNotBlank() &&
                                    viewModel.selectedCategory.value != null
                        } else {
                            viewModel.individualTitles.take(viewModel.sessionCount.intValue)
                                .all { it.isNotBlank() } &&
                                    viewModel.individualCategories.take(viewModel.sessionCount.intValue)
                                        .all { it != null }
                        }
                        )

                QuietCraftTheme.QuietCraftFilledButton(
                    label = "Save Plan",
                    enabled = isFormValid,
                    onClick = {
                        if (viewModel.isGlobalMode.value) {
                            viewModel.saveCurrentGlobalPlan()
                        } else {
                            // Update durations from time pickers
                            perSessionStarts.indices.forEach { index ->
                                if (index < viewModel.individualDurations.size) {
                                    viewModel.individualDurations[index] =
                                        ((perSessionEnds[index] - perSessionStarts[index]) / (60 * 1000)).toInt()
                                }
                            }
                            viewModel.saveCurrentIndividualPlan()
                        }
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}





//Companion Composable Function
@OptIn(ExperimentalMaterial3Api::class)
@Composable

//BREAK SETTING SECTION

fun BreakSettingsSection(viewModel: HyperFocusViewModel) {
    val spacing = QuietCraftTheme.Spacing
    val context = LocalContext.current

    var showShortDurationDialog by remember { mutableStateOf(false) }
    var showShortFrequencyDialog by remember { mutableStateOf(false) }
    var showLongDurationDialog by remember { mutableStateOf(false) }
    var showLongFrequencyDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.large)
    ) {
        Text(
            "Break Settings",
            style = QuietCraftTheme.subtleLabelStyle,
            modifier = Modifier.padding(bottom = spacing.small)
        )

        // Short Break Duration
        BreakSettingRow(
            label = "Short Break Duration",
            value = "${viewModel.shortBreakDuration.intValue} min",
            icon = Icons.TwoTone.AirlineSeatReclineNormal,
            onClick = { showShortDurationDialog = true }
        )

        // Short Break Frequency
        BreakSettingRow(
            label = "Short Break Frequency",
            value = "After every ${viewModel.shortBreakFrequency.intValue} sessions",
            icon = Icons.TwoTone.RepeatOne,
            onClick = { showShortFrequencyDialog = true }
        )

        // Long Break Duration
        BreakSettingRow(
            label = "Long Break Duration",
            value = "${viewModel.longBreakDuration.intValue} min",
            icon = Icons.TwoTone.Hotel,
            onClick = { showLongDurationDialog = true }
        )

        // Long Break Frequency
        BreakSettingRow(
            label = "Long Break Frequency",
            value = "After every ${viewModel.longBreakFrequency.intValue} sessions",
            icon = Icons.TwoTone.Repeat,
            onClick = { showLongFrequencyDialog = true }
        )
    }

    if (showShortDurationDialog) {
        FrequencyInputDialog(
            initial = viewModel.shortBreakDuration.intValue,
            onConfirm = {
                viewModel.shortBreakDuration.intValue = it
                showShortDurationDialog = false
            },
            onDismiss = { showShortDurationDialog = false },
            label = "Short Break Duration (min)",
            validate = { it > 0 }
        )
    }

    if (showShortFrequencyDialog) {
        FrequencyInputDialog(
            initial = viewModel.shortBreakFrequency.intValue,
            onConfirm = {
                viewModel.shortBreakFrequency.intValue = it
                showShortFrequencyDialog = false
            },
            onDismiss = { showShortFrequencyDialog = false },
            label = "Short Break Frequency",
            validate = { it in 1..viewModel.sessionCount.intValue }
        )
    }

    if (showLongDurationDialog) {
        FrequencyInputDialog(
            initial = viewModel.longBreakDuration.intValue,
            onConfirm = {
                viewModel.longBreakDuration.intValue = it
                showLongDurationDialog = false
            },
            onDismiss = { showLongDurationDialog = false },
            label = "Long Break Duration (min)",
            validate = { it > viewModel.shortBreakDuration.intValue }
        )
    }

    if (showLongFrequencyDialog) {
        FrequencyInputDialog(
            initial = viewModel.longBreakFrequency.intValue,
            onConfirm = {
                viewModel.longBreakFrequency.intValue = it
                showLongFrequencyDialog = false
            },
            onDismiss = { showLongFrequencyDialog = false },
            label = "Long Break Frequency",
            validate = { it > viewModel.shortBreakFrequency.intValue && it <= viewModel.sessionCount.intValue }
        )
    }
}

// Extension functions for time conversions
fun Long.toMinutes() = this / (60 * 1000)
fun Int.minutesToMillis() = this * 60 * 1000L


@Composable
fun BreakSettingRow(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

