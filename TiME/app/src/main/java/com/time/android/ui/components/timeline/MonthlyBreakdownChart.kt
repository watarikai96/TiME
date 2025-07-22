package com.time.android.ui.components.timeline

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.time.android.model.Category
import com.time.android.model.TiME
import java.time.*

@Composable
fun MonthlyBreakdownChart(
    entries: List<TiME>,
    categories: List<Category>,
    selectedFormat: TimeFormatOption,
    modifier: Modifier = Modifier
)
 {
    val zoneId = ZoneId.systemDefault()
    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    val allDates = (1..daysInMonth).map { currentMonth.atDay(it) }

    val dailyCategoryMinutes = allDates.map { date ->
        categories.associate { category ->
            val totalMin = entries.filter {
                !it.isBreak &&
                        it.category == category.id &&
                        Instant.ofEpochMilli(it.startTime).atZone(zoneId).toLocalDate() == date
            }.sumOf { (it.endTime - it.startTime).coerceAtLeast(0L) } / 60000f
            category.id to totalMin
        }
    }

    val maxTotal = dailyCategoryMinutes.maxOfOrNull { it.values.sum() } ?: 1f
    val avgTotal = dailyCategoryMinutes.map { it.values.sum() }.average().toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            dailyCategoryMinutes.forEachIndexed { index, categoryMap ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            categories.forEach { category ->
                                val minutes = categoryMap[category.id] ?: 0f
                                if (minutes > 0f) {
                                    val animatedHeight by animateFloatAsState(
                                        targetValue = (minutes / maxTotal),
                                        animationSpec = tween(600),
                                        label = "monthlyStackAnim"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(animatedHeight)
                                            .background(Color(category.color.toInt()))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    if ((index + 1) % 2 == 0) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    } else {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Average: ${formatDuration((avgTotal * 60_000).toLong(), selectedFormat)} / day",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

    }
}
