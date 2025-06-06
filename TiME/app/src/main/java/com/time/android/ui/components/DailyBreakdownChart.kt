package com.time.android.ui.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.time.android.model.Category
import com.time.android.model.TiME
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun DailyBreakdownChart(
    date: LocalDate,
    entries: List<TiME>,
    categories: List<Category>,
    selectedFormat: TimeFormatOption,
    modifier: Modifier = Modifier
)
 {
    val zoneId = ZoneId.systemDefault()
    val dailyEntries = entries.filter {
        Instant.ofEpochMilli(it.startTime).atZone(zoneId).toLocalDate() == date && !it.isBreak
    }

    val categoryTotals = categories.map { category ->
        val total = dailyEntries.filter { it.category == category.id }
            .sumOf { (it.endTime - it.startTime).coerceAtLeast(0L) } / 60000f
        category to total
    }.filter { it.second > 0f }

    val maxMinutes = categoryTotals.maxOfOrNull { it.second } ?: 1f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {

        categoryTotals.forEach { (category, minutes) ->
            val animatedFraction by animateFloatAsState(
                targetValue = (minutes / maxMinutes).coerceIn(0f, 1f),
                animationSpec = tween(500),
                label = "dailyBarAnim"
            )

            Column(Modifier.padding(vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatDuration((minutes * 60_000L).toLong(), selectedFormat),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedFraction)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(category.color.toInt()))
                    )
                }
            }
        }
    }
}
