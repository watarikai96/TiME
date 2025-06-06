package com.time.android.ui.screens

import com.time.android.viewmodel.HyperFocusViewModel
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.twotone.Bolt
import androidx.compose.material.icons.twotone.Celebration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.time.android.ui.components.AnimatedClockLoader
import com.time.android.ui.components.EmptyStateView
import com.time.android.ui.components.FocusStreakBadge
import com.time.android.ui.components.TimeFormatOption
import com.time.android.ui.components.TimelineEntryItem
import com.time.android.ui.components.formatDuration
import com.time.android.ui.components.hyperfocus.HyperFocusSessionCarousel
import com.time.android.ui.theme.QuietCraftTheme
import com.time.android.ui.theme.ThemeManager
import com.time.android.viewmodel.CategoryViewModel
import com.time.android.viewmodel.TiMEViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TimelineScreen(
    modifier: Modifier = Modifier,
    viewModel: TiMEViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    selectedFormat: TimeFormatOption,
    navController: NavHostController = rememberNavController(),
    hyperFocusViewModel: HyperFocusViewModel = viewModel()
) {


    val listState = rememberLazyListState()

    LaunchedEffect(hyperFocusViewModel.currentSessionIndex.intValue) {
        listState.animateScrollToItem(hyperFocusViewModel.currentSessionIndex.intValue)
    }



    val categories by categoryViewModel.categories.collectAsState()
    val accentColor = ThemeManager.accentColor()
    val queue = hyperFocusViewModel.sessionQueue


    LaunchedEffect(Unit) { categoryViewModel.loadCategories() }

    val today = LocalDate.now()
    val todayFormatted = today.format(DateTimeFormatter.ofPattern("MMMM d yyyy"))
    val timeList by remember { derivedStateOf { viewModel.todaySessions } }

    val groupedTimelineEntries = timeList.groupBy { it.category.ifBlank { "Uncategorized" } }
    val groupedHyperFocusSessions = queue.groupBy { it.planTitle ?: "Untitled Plan" }

    val collapsedStates = remember { mutableStateMapOf<String, Boolean>() }

    val totalFocusMin = timeList
        .filter { !it.isBreak && !it.isPaused && it.isCompleted }
        .sumOf { (it.endTime - it.startTime).coerceAtLeast(0L) } / 60_000

    val focusFraction = (totalFocusMin / 1440f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = focusFraction,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "AnimatedFocusBar"
    )


    val streak = remember { derivedStateOf { viewModel.getFocusStreak() } }.value


    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->

            LazyColumn(
                modifier = modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Header Date + Focus Progress
                stickyHeader {
                    Surface(
                        tonalElevation = 3.dp,
                        shadowElevation = 1.dp,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {


                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = today.dayOfWeek
                                            .getDisplayName(TextStyle.FULL, Locale.getDefault())
                                            .uppercase(),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.25.sp
                                        ),
                                        color = accentColor
                                    )
                                    Text(
                                        text = todayFormatted,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }

                                if (streak >= 2) {
                                    FocusStreakBadge(streak)
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Focus Duration",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.15.sp
                                    )
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(30.dp),
                                    tonalElevation = 2.dp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${formatDuration(totalFocusMin * 60_000L, selectedFormat)} / 24h",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.2.sp,
                                            color = accentColor
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Progress Bar
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .graphicsLayer {
                                            scaleX = animatedProgress
                                            transformOrigin = TransformOrigin(0f, 0f)
                                        }
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    accentColor,
                                                    accentColor.copy(alpha = 0.6f),
                                                    accentColor.copy(alpha = 0.3f)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                // HYPERFOCUS CARD CAROUSEL
                val currentIndex = hyperFocusViewModel.currentSessionIndex.intValue
                val isPaused = hyperFocusViewModel.isPaused.value

                groupedHyperFocusSessions.forEach { (planTitle, sessions) ->
                    item {
                        Column(
                             verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            QuietCraftTheme.QuietCraftSectionHeader(
                                icon = Icons.TwoTone.Bolt,
                                title = planTitle
                            )
                            HyperFocusSessionCarousel(
                                listState = listState,
                                hyperFocusViewModel = hyperFocusViewModel,
                                sessions = sessions,
                                currentIndex = currentIndex,
                                isPaused = isPaused,
                                onPause = { hyperFocusViewModel.pauseSession() },
                                onResume = { hyperFocusViewModel.resumeSession() },
                                onEnd = { hyperFocusViewModel.endSession() },
                                onDelete = { hyperFocusViewModel.deleteSession(it) },
                                onDurationChange = { index, newDuration ->
                                    hyperFocusViewModel.adjustSessionDuration(index, newDuration)
                                }
                            )
                        }
                    }
                }

                //  Empty State
                if (timeList.isEmpty()) {
                    item {
                        Spacer(Modifier.height(32.dp))
                        EmptyStateView(
                            message = "No sessions for today!",
                            icon = Icons.TwoTone.Celebration
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }

                //  Show loading state if categories aren't ready
                if (categories.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(top = 120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedClockLoader(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                    return@LazyColumn
                }


                //  Category Sections
                groupedTimelineEntries.forEach { (categoryId, entries) ->
                    val category = categories.find { it.id == categoryId }
                    val categoryColor = category?.color?.toInt() ?: 0xFFCCCCCC.toInt()
                    val isCollapsed = collapsedStates[categoryId] == true

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { collapsedStates[categoryId] = !isCollapsed }
                                .padding(top = 8.dp, bottom = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(categoryColor))
                            )
                            Text(
                                text = category?.name ?: "Uncategorized",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                            Text(
                                "${entries.sumOf { it.endTime - it.startTime } / 60_000} min",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = "Toggle Collapse"
                            )
                        }
                    }

                    if (!isCollapsed) {
                        items(entries, key = { it.id }) { entry ->
                            TimelineEntryItem(
                                entry = entry,
                                categories = categories,
                                selectedFormat = selectedFormat,
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
                                onTogglePause = { viewModel.update(it) }
                            )

                        }
                    }
                }
            }
        }
    }
}