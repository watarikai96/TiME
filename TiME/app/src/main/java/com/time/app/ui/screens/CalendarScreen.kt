package com.time.app.ui.screens


import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.twotone.Event
import androidx.compose.material.icons.twotone.FilterList
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.time.app.ui.components.CalendarEvents
import com.time.app.ui.components.EmptyStateView
import com.time.app.ui.components.TimeFormatOption
import com.time.app.ui.components.TimelineEntryItem
import com.time.app.ui.theme.ThemeManager
import com.time.app.viewmodel.CategoryViewModel
import com.time.app.viewmodel.TiMEViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class FilterMode { WEEK, MONTH, YEAR }


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@SuppressLint("NewApi")
@Composable
fun CalendarViewScreen(
    modifier: Modifier = Modifier,
    viewModel: TiMEViewModel = viewModel(),
    selectedFormat: TimeFormatOption,
    categoryViewModel: CategoryViewModel = viewModel(),



) {

    val allEntries = viewModel.timeList
    val categories by categoryViewModel.categories.collectAsState()
    val accentColor = ThemeManager.accentColor()
    val selectedFormat = rememberSaveable { mutableStateOf(TimeFormatOption.HOURS_MINUTES) }.value
    var showFilterSheet by remember { mutableStateOf(false) }
    var filterMode by remember { mutableStateOf(FilterMode.WEEK) }
    var searchQuery by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(sheetState.currentValue) {
        if (!sheetState.isVisible) showFilterSheet = false
    }

    LaunchedEffect(Unit) { categoryViewModel.loadCategories() }

    var showPicker by remember { mutableStateOf(false) }
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }


    val datePickerState = rememberDatePickerState()


    LaunchedEffect(today) {
        datePickerState.selectedDateMillis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }



    val initialPage = 1000
    val pagerState = rememberPagerState(initialPage = initialPage)

    val currentWeekStart = remember(pagerState.currentPage) {
        today.minusDays(today.dayOfWeek.ordinal.toLong())
            .plusWeeks((pagerState.currentPage - initialPage).toLong())
    }

//Filter Logic
    val filteredEntries = allEntries.filter { entry ->
        val entryDate = Instant.ofEpochMilli(entry.startTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val matchesSearch = entry.title.contains(searchQuery, ignoreCase = true)

        val isInRange = if (searchQuery.isNotBlank()) {
            when (filterMode) {
                FilterMode.WEEK -> {
                    val start = selectedDate.with(DayOfWeek.MONDAY)
                    val end = start.plusDays(6)
                    entryDate in start..end
                }
                FilterMode.MONTH -> {
                    val start = selectedDate.withDayOfMonth(1)
                    val end = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
                    entryDate in start..end
                }
                FilterMode.YEAR -> {
                    val start = selectedDate.withDayOfYear(1)
                    val end = selectedDate.withMonth(12).withDayOfMonth(31)
                    entryDate in start..end
                }
            }
        } else {
            // Default: show only the selected day
            entryDate == selectedDate
        }

        isInRange && matchesSearch
    }


    Box {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = { showPicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Pick Date")
                    Spacer(Modifier.width(4.dp))
                    Text(text = selectedDate.format(DateTimeFormatter.ofPattern("MMM d")))
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { searchQuery = "" }
                                    .padding(end = 4.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = accentColor.copy(alpha = 0.5f),
                        focusedBorderColor = accentColor,
                        cursorColor = accentColor,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )

                )

                IconButton(onClick = { showFilterSheet = true }) {
                    Icon(Icons.TwoTone.FilterList, contentDescription = "Filter Mode")
                }
            }

            // Show Holiday Ribbon
            CalendarEvents(date = selectedDate)


            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Week of ${currentWeekStart.format(DateTimeFormatter.ofPattern("MMM d"))}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
                    )
                    HorizontalPager(
                        state = pagerState,
                        count = Int.MAX_VALUE,
                        modifier = Modifier.fillMaxWidth().height(70.dp)
                    ) { page ->
                        val weekStart = today.minusDays(today.dayOfWeek.ordinal.toLong()).plusWeeks((page - initialPage).toLong())
                        val days = List(7) { weekStart.plusDays(it.toLong()) }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            days.forEach { date ->
                                val isSelected = date == selectedDate
                                val isToday = date == today
                                val sessionColors = allEntries.filter {
                                    Instant.ofEpochMilli(it.startTime).atZone(ZoneId.systemDefault()).toLocalDate() == date
                                }.mapNotNull { entry ->
                                    categories.find { it.id == entry.category }?.color?.toInt()
                                }.distinct()

                                val backgroundColor by animateColorAsState(
                                    if (isToday) MaterialTheme.colorScheme.surface else Color.Transparent,
                                    label = "TodayHighlight"
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(backgroundColor)
                                        .clickable { selectedDate = date }
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = date.dayOfWeek.name.take(3),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "${date.dayOfMonth}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        sessionColors.take(3).forEach { colorInt ->
                                            Box(
                                                modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(colorInt))
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Spacer(Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier.height(2.dp).fillMaxWidth().background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (filteredEntries.isEmpty()) {
                EmptyStateView(
                    message = "No sessions on this date!",
                    icon = Icons.TwoTone.Event
                )
            }


            //TIMELINE CARD
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
            ) {
                items(filteredEntries, key = { it.id }) { entry ->
                    TimelineEntryItem(
                        entry = entry,
                        categories = categories,
                        onDelete = { viewModel.delete(it) },
                        onToggleCompleted = { viewModel.update(it) },
                        onUpdate = { updated ->
                            val original = viewModel.timeList.find { it.id == updated.id }
                            if (original != null) {
                                viewModel.update(updated.copy(isPaused = original.isPaused))
                            } else {
                                viewModel.update(updated)
                            }
                        },
                        onTogglePause = { viewModel.update(it) },
                        selectedFormat = selectedFormat,
                    )
                }
            }

        }
    }

    //DATE PICKER

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filter By", style = MaterialTheme.typography.titleMedium)
                FilterMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                filterMode = mode
                                showFilterSheet = false
                            }
                            .background(
                                if (mode == filterMode)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else Color.Transparent
                            )
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        RadioButton(selected = (mode == filterMode), onClick = null)
                        Spacer(Modifier.width(8.dp))
                        Text(mode.name.lowercase().replaceFirstChar { it.uppercaseChar() }, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}