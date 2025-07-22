package com.time.android.ui.components.timeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.concurrent.TimeUnit

enum class TimeFormatOption(val label: String) {
    MINUTES_ONLY("Minutes Only"),
    HOURS_MINUTES("Hours + Minutes"),
    HOURS_MINUTES_SECONDS("Hours + Minutes + Seconds")
}

@Composable
fun TimeFormatSelector(
    selected: TimeFormatOption,
    onSelect: (TimeFormatOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Preferred Time Format",
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TimeFormatOption.entries.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .padding(vertical = 6.dp)
            ) {
                RadioButton(
                    selected = selected == option,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

//HELPERS

fun formatDuration(
    durationMillis: Long,
    format: TimeFormatOption
): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when (format) {
        TimeFormatOption.MINUTES_ONLY -> "${(totalSeconds / 60)}m"
        TimeFormatOption.HOURS_MINUTES -> {
            if (hours > 0) {
                "${hours}h ${minutes}m"
            } else {
                "${minutes}m"
            }
        }
        TimeFormatOption.HOURS_MINUTES_SECONDS -> {
            val parts = mutableListOf<String>()
            if (hours > 0) parts.add("${hours}h")
            if (minutes > 0 || hours > 0) parts.add("${minutes}m")
            parts.add("${seconds}s")
            parts.joinToString(" ")
        }
    }
}

