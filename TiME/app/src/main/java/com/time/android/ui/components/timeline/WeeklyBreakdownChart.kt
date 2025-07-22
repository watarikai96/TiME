package com.time.android.ui.components.timeline

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.time.android.model.Category
import com.time.android.model.TiME
import java.time.*
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeeklyBreakdownChart(
    entries: List<TiME>,
    categories: List<Category>,
    selectedFormat: TimeFormatOption,
    modifier: Modifier = Modifier
)
 {
    val today = LocalDate.now()
    val weekStart = today.with(DayOfWeek.MONDAY)
    val days = (0..6).map { weekStart.plusDays(it.toLong()) }
    val zoneId = ZoneId.systemDefault()

    val dailyCategoryMinutes = days.map { date ->
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                            .width(12.dp)
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
                                    val animatedFraction by animateFloatAsState(
                                        targetValue = (minutes / maxTotal).coerceIn(0f, 1f),
                                        animationSpec = tween(500),
                                        label = "stackedAnim"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(animatedFraction)
                                            .background(Color(category.color.toInt()))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    val label = days[index].dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val totalMillis = (categoryMap.values.sum() * 60_000L).toLong()

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Text(
                            text = formatDuration(totalMillis, selectedFormat),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                }
            }
        }
    }
}
