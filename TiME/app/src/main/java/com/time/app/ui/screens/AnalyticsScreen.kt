package com.time.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AdfScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.time.app.ui.components.AnalyticsOverviewHeader
import com.time.app.ui.components.AnalyticsSummaryInsights
import com.time.app.ui.components.CategoryBreakdownChart
import com.time.app.ui.components.DailyBreakdownChart
import com.time.app.ui.components.MonthlyBreakdownChart
import com.time.app.ui.components.ReportGeneratorModal
import com.time.app.ui.components.TimeFormatOption
import com.time.app.ui.components.WeeklyBreakdownChart
import com.time.app.viewmodel.CategoryViewModel
import com.time.app.viewmodel.TiMEViewModel
import java.time.LocalDate

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: TiMEViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    selectedFormat: TimeFormatOption // ← Add this
) {
    val entries = viewModel.timeList
    val categories by categoryViewModel.categories.collectAsState()
    val scrollState = rememberScrollState()
    val today = LocalDate.now()
    var selectedView by remember { mutableStateOf("WEEKLY") }
    var showReportModal by remember { mutableStateOf(false) } // Add this

    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
        // Toggle Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("DAILY", "WEEKLY", "MONTHLY").forEach { label ->
                Button(
                    onClick = { selectedView = label },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedView == label)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedView == label)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }

        AnalyticsOverviewHeader(
            entries = entries,
            selectedFormat = selectedFormat,
            modifier = Modifier.fillMaxWidth()
        )

        AnalyticsSummaryInsights(
            entries = entries,
            categories = categories,
            selectedFormat = selectedFormat
        )


        AnimatedContent(
            targetState = selectedView,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "analyticsViewSwitcher"
        ) { viewMode ->
            Column {
                Text(
                    text = when (viewMode) {
                        "DAILY" -> "Daily Focus"
                        "WEEKLY" -> "Weekly Focus"
                        "MONTHLY" -> "Monthly Focus"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                when (viewMode) {
                    "DAILY" -> DailyBreakdownChart(today, entries, categories, selectedFormat)
                    "WEEKLY" -> WeeklyBreakdownChart(entries, categories, selectedFormat)
                    "MONTHLY" -> MonthlyBreakdownChart(entries, categories, selectedFormat)
                }

            }
        }

        Text(
            "Category Breakdown",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        CategoryBreakdownChart(entries = entries, categories = categories)

        Spacer(Modifier.height(80.dp))
    }


// Report Generator FAB
Box(
modifier = Modifier
.fillMaxSize()
.padding(bottom = 16.dp, end = 16.dp),
contentAlignment = Alignment.BottomEnd
) {
    FloatingActionButton(
        onClick = { showReportModal = true },
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Icon(
            Icons.TwoTone.AdfScanner,
            contentDescription = "Generate Report",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
}

// Report modal
if (showReportModal) {
    ReportGeneratorModal(
        entries = entries,
        categories = categories,
        selectedFormat = selectedFormat,
        onDismiss = { showReportModal = false }
    )
}
}