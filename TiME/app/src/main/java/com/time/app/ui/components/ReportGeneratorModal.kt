package com.time.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.time.app.R
import com.time.app.model.Category
import com.time.app.model.TiME
import com.time.app.ui.theme.QuietCraftTheme
import com.time.app.ui.theme.ThemeManager
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportGeneratorModal(
    entries: List<TiME>,
    categories: List<Category>,
    selectedFormat: TimeFormatOption,
    onDismiss: () -> Unit
) {

    var showDateRangePicker by remember { mutableStateOf(false) }
    val selectedStartDate = remember { mutableStateOf<LocalDate?>(null) }
    val selectedEndDate = remember { mutableStateOf<LocalDate?>(null) }
    val accentColor = ThemeManager.accentColor()
    val now = ZonedDateTime.now()
    // Theme colors
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val borderColor = MaterialTheme.colorScheme.outlineVariant


    val dateRangeState = rememberDateRangePickerState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = backgroundColor,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with title and export button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Focus Sessions Report",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary stats in a single row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Total Focus",
                    value = formatDuration(
                        entries.filter { !it.isBreak }.sumOf { it.endTime - it.startTime },
                        selectedFormat
                    ),
                    accentColor = accentColor,
                    textColor = textColor
                )

                StatItem(
                    title = "Sessions",
                    value = entries.size.toString(),
                    accentColor = accentColor,
                    textColor = textColor
                )

                StatItem(
                    title = "Avg Session",
                    value = formatDuration(
                        if (entries.isNotEmpty()) entries.sumOf { it.endTime - it.startTime } / entries.size else 0,
                        selectedFormat
                    ),
                    accentColor = accentColor,
                    textColor = textColor
                )
            }


            // Date range selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(highlightColor)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedStartDate.value?.format(DateTimeFormatter.ofPattern("MMM d"))
                            ?: "Start",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = selectedStartDate.value?.format(DateTimeFormatter.ofPattern("yyyy"))
                            ?: "date",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = "to",
                    tint = secondaryTextColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedEndDate.value?.format(DateTimeFormatter.ofPattern("MMM d"))
                            ?: "End",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = selectedEndDate.value?.format(DateTimeFormatter.ofPattern("yyyy"))
                            ?: "date",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = { showDateRangePicker = true },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = accentColor
                    )
                ) {
                    Text("Edit", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Database table

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(surfaceColor)
                    .border(1.dp, borderColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accentColor.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableHeaderText("Category",modifier = Modifier.weight(1.5f))
                    TableHeaderText("Duration",modifier = Modifier.weight(1.1f))
                    TableHeaderText("Date",modifier = Modifier.weight(1f))
                    TableHeaderText("Time",modifier = Modifier.weight(1f))
                    TableHeaderText("Session",modifier = Modifier.weight(1.5f))
                }


                // Filtered entries
                val filteredEntries = entries.filter {
                    val entryDate = Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate()
                    when {
                        selectedStartDate.value == null && selectedEndDate.value == null -> true
                        selectedStartDate.value != null && selectedEndDate.value != null ->
                            entryDate in selectedStartDate.value!!..selectedEndDate.value!!
                        else -> true
                    }
                }.sortedByDescending { it.startTime }

                filteredEntries.forEachIndexed { index, entry ->
                    val category = categories.find { it.id == entry.category }
                    val startDateTime = Instant.ofEpochMilli(entry.startTime).atZone(ZoneId.systemDefault())
                    val endDateTime = Instant.ofEpochMilli(entry.endTime).atZone(ZoneId.systemDefault())

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.04f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category column
                        Row(
                            modifier = Modifier.weight(1.5f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(category?.color?.toInt() ?: 0xFF888888.toInt()))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category?.name ?: "Uncategorized",
                                style = MaterialTheme.typography.labelMedium,
                                color = textColor,
                                modifier = Modifier.weight(1.5f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Duration
                        Text(
                            text = formatDuration(entry.endTime - entry.startTime, selectedFormat),
                            style = MaterialTheme.typography.labelSmall,
                            color = secondaryTextColor,
                            modifier = Modifier.weight(0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )

                        // Date
                        Text(
                            text = startDateTime.format(DateTimeFormatter.ofPattern("MMM d")),
                            style = MaterialTheme.typography.labelSmall,
                            color = secondaryTextColor,
                            modifier = Modifier.weight(0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )

                        // Time
                        Text(
                            text = "${startDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))} - ${endDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = secondaryTextColor,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Clip
                        )

                        // Session title
                        Text(
                            text = entry.title.ifEmpty { "Untitled Session" },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = textColor,
                            modifier = Modifier.weight(1.5f),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }



            // Footer with generation timestamp
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Generated on ${now.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = secondaryTextColor,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )


//    // Date range picker dialog
            if (showDateRangePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDateRangePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                dateRangeState.selectedStartDateMillis?.let {
                                    selectedStartDate.value = Instant.ofEpochMilli(it)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                dateRangeState.selectedEndDateMillis?.let {
                                    selectedEndDate.value = Instant.ofEpochMilli(it)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                showDateRangePicker = false
                            }
                        ) {
                            Text("DONE", style = MaterialTheme.typography.labelMedium)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDateRangePicker = false }
                        ) {
                            Text("CANCEL", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                ) {
                    DateRangePicker(
                        state = dateRangeState,
                        title = {
                            Text(
                                text = "Select Date Range",
                                modifier = Modifier.padding(16.dp),
                                style = QuietCraftTheme.sectionHeaderStyle.copy(
                                    color = textColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        },
                        headline = null
                    )
                }
            }
        }
    }
}



// HELPER FUNCTIONS
@Composable
private fun StatItem(
    title: String,
    value: String,
    accentColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
private fun TableHeaderText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.Bold
        ),
        softWrap = true,
        maxLines = Int.MAX_VALUE,
        overflow = TextOverflow.Clip,
        modifier = modifier.padding(end = 8.dp)

    )
}