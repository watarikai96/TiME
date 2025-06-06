package com.time.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Category
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.time.android.model.Category
import com.time.android.model.TiME
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.collections.find

@Composable
fun AnalyticsOverviewHeader(
    entries: List<TiME>,
    selectedFormat: TimeFormatOption,
    modifier: Modifier = Modifier
) {
    val zoneId = ZoneId.systemDefault()
    val today = LocalDate.now()
    val weekStart = today.with(DayOfWeek.MONDAY)
    val weekEnd = weekStart.plusDays(6)

    val weekly = entries.filter {
        val date = Instant.ofEpochMilli(it.startTime).atZone(zoneId).toLocalDate()
        date in weekStart..weekEnd
    }

    val totalFocusMin = weekly.filter { !it.isBreak && !it.isPaused && it.isCompleted }
        .sumOf { (it.endTime - it.startTime).coerceAtLeast(0L) } / 60000

    val breakMin = weekly.filter { it.isBreak }
        .sumOf { (it.endTime - it.startTime).coerceAtLeast(0L) } / 60000 +
            weekly.filter { it.isPaused }
                .sumOf { it.breakTimeTotal?.toInt() ?: 0 }

    val daysCount = ChronoUnit.DAYS.between(weekStart, weekEnd) + 1
    val avgPerDay = if (daysCount > 0) totalFocusMin / daysCount else 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FocusStatCard("Total Focus", formatDuration(totalFocusMin * 60_000L, selectedFormat), Modifier.weight(1f))
        FocusStatCard("Avg / Day", formatDuration(avgPerDay * 60_000L, selectedFormat), Modifier.weight(1f))
        FocusStatCard("Break Time", formatDuration(breakMin * 60_000L, selectedFormat), Modifier.weight(1f))

    }
}


@Composable
fun FocusStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}



@Composable
fun AnalyticsSummaryInsights(
            entries: List<TiME>,
            categories: List<Category>,
            selectedFormat: TimeFormatOption,
            modifier: Modifier = Modifier) {
    val nonBreak = entries.filter { !it.isBreak && it.isCompleted }

    val mostUsed = nonBreak
        .groupBy { it.category }
        .maxByOrNull { it.value.sumOf { s -> s.endTime - s.startTime } }?.key

    val longest = nonBreak.maxByOrNull { it.endTime - it.startTime }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (mostUsed != null) {
            val name = categories.find { it.id == mostUsed }?.name ?: mostUsed

            InsightStatChip(
                icon = Icons.TwoTone.Category,
                label = "Most Used Category",
                value = name
            )
        }

        if (longest != null) {
            val duration = longest.endTime - longest.startTime
            InsightStatChip(
                icon = Icons.TwoTone.Timer,
                label = "Longest Session",
                value = "${longest.title} (${formatDuration(duration, selectedFormat)})"
            )
        }
    }
}

@Composable
fun InsightStatChip(
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
