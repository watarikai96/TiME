package com.time.app.ui.components.hyperfocus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.time.app.ui.theme.QuietCraftTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionTimePicker(
    label: String,
    startTime: Long,
    endTime: Long,
    maxDate: Long,
    onTimeSelected: (Long, Long) -> Unit
) {
    val context = LocalContext.current
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    // Convert times to Calendar instances for better handling
    val startCal = remember(startTime) { Calendar.getInstance().apply { timeInMillis = startTime } }
    val endCal = remember(endTime) { Calendar.getInstance().apply { timeInMillis = endTime } }

    val startFormatted = remember(startTime) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(startTime))
    }
    val endFormatted = remember(endTime) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(endTime))
    }
    val durationMinutes = remember(startTime, endTime) {
        TimeUnit.MILLISECONDS.toMinutes(endTime - startTime)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = QuietCraftTheme.subtleLabelStyle)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Start Time Button
            OutlinedButton(
                onClick = { showStartPicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(startFormatted)
            }

            // End Time Button
            OutlinedButton(
                onClick = { showEndPicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(endFormatted)
            }
        }

        Text(
            "Duration: $durationMinutes minutes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }

    // Start Time Picker Dialog
    if (showStartPicker) {
        val initialHour = startCal.get(Calendar.HOUR_OF_DAY)
        val initialMinute = startCal.get(Calendar.MINUTE)
        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute
        )

        AlertDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newStartCal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val newStart = newStartCal.timeInMillis
                        val newEnd = maxOf(endTime, newStart + 5 * 60 * 1000L)
                        onTimeSelected(newStart, newEnd)
                        showStartPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartPicker = false }
                ) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    // End Time Picker Dialog
    if (showEndPicker) {
        val initialHour = endCal.get(Calendar.HOUR_OF_DAY)
        val initialMinute = endCal.get(Calendar.MINUTE)
        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute
        )

        AlertDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newEndCal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val newEnd = newEndCal.timeInMillis
                        if (newEnd > startTime + 5 * 60 * 1000L) { // Ensure minimum 5 minute duration
                            onTimeSelected(startTime, newEnd)
                        } else {
                            val adjustedEnd = startTime + 5 * 60 * 1000L
                            onTimeSelected(startTime, adjustedEnd)
                        }
                        showEndPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndPicker = false }
                ) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

