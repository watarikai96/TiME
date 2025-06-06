package com.time.android.ui.components.reusables

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.time.android.ui.theme.QuietCraftDurationChip
import com.time.android.ui.theme.QuietCraftTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangeCard(
    selectedDate: Calendar,
    startTime: Long,
    endTime: Long,
    onDateTimeChange: (Calendar, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.timeInMillis
    )


    var showDatePicker by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    QuietCraftTheme.QuietCraftCard(modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Date & Time",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }


            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                QuietCraftTheme.QuietCraftLabel("Date")
                QuietCraftTheme.QuietCraftDurationChip(
                    icon = Icons.Outlined.CalendarToday,
                    label = dateFormatter.format(selectedDate.time),
                    onClick = { showDatePicker = true }
                )

                QuietCraftTheme.QuietCraftLabel("Start Time")
                QuietCraftTheme.QuietCraftDurationChip(
                    icon = Icons.Outlined.AccessTime,
                    label = timeFormatter.format(Date(startTime)),
                    onClick = { showStartPicker = true }
                )

                QuietCraftTheme.QuietCraftLabel("End Time")
                QuietCraftTheme.QuietCraftDurationChip(
                    icon = Icons.Outlined.AccessTime,
                    label = timeFormatter.format(Date(endTime)),
                    onClick = { showEndPicker = true }
                )

                QuietCraftTheme.QuietCraftLabel("Quick Duration")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 30, 45, 60, 90).forEach { min ->
                        FilterChip(
                            selected = (endTime - startTime) == min * 60000L,
                            onClick = {
                                onDateTimeChange(
                                    selectedDate,
                                    startTime,
                                    startTime + min * 60000L
                                )
                            },
                            label = { Text("${min}min") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {datePickerState.selectedDateMillis?.let {
                    val updated = Calendar.getInstance().apply { timeInMillis = it }
                    onDateTimeChange(updated, startTime, endTime)
                }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }

    if (showStartPicker) {
        val cal = Calendar.getInstance().apply { timeInMillis = startTime }
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val updatedStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }.timeInMillis
                if (updatedStart < endTime) {
                    onDateTimeChange(selectedDate, updatedStart, endTime)
                } else {
                    Toast.makeText(context, "Start must be before end", Toast.LENGTH_SHORT).show()
                }
                showStartPicker = false
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false // Force 12-hour format with AM/PM
        ).show()
    }

    if (showEndPicker) {
        val cal = Calendar.getInstance().apply { timeInMillis = endTime }
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val updatedEnd = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }.timeInMillis
                if (updatedEnd > startTime) {
                    onDateTimeChange(selectedDate, startTime, updatedEnd)
                } else {
                    Toast.makeText(context, "End must be after start", Toast.LENGTH_SHORT).show()
                }
                showEndPicker = false
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false // Force 12-hour format with AM/PM
        ).show()
    }
}

