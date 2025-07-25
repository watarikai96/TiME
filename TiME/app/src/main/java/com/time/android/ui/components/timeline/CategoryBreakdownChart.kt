package com.time.android.ui.components.timeline

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
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CategoryBreakdownChart(
    entries: List<TiME>,
    categories: List<Category>,
    modifier: Modifier = Modifier,
) {
    val zoneId = ZoneId.systemDefault()
    val weekStart = LocalDate.now().with(DayOfWeek.MONDAY)
    val weekEnd = weekStart.plusDays(6)

    val filtered = entries.filter {
        val date = Instant.ofEpochMilli(it.startTime).atZone(zoneId).toLocalDate()
        date in weekStart..weekEnd && !it.isBreak
    }

    val categoryTotals = categories.map { category ->
        val total = filtered.filter { it.category == category.id }
            .sumOf { (it.endTime - it.startTime).coerceAtLeast(0L) } / 60000f
        category to total
    }.filter { it.second > 0f }

    val maxTotal = categoryTotals.maxOfOrNull { it.second } ?: 1f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {

        categoryTotals.forEach { (category, minutes) ->
            val fillFraction by animateFloatAsState(
                targetValue = (minutes / maxTotal).coerceIn(0f, 1f),
                animationSpec = tween(500),
                label = "categoryFillAnim"
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
                        text = "${minutes.toInt()} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fillFraction)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(category.color.toInt()))
                    )
                }
            }
        }
    }
}
