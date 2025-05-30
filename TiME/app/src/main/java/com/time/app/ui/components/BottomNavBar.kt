package com.time.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.time.app.navigation.Screen

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    onAddClick: () -> Unit
) {
    val navItems = listOf(
        Screen.List to Icons.Default.Schedule,
        Screen.Calendar to Icons.Default.CalendarToday,
        null, // Center FAB
        Screen.Analytics to Icons.Default.BarChart,
        Screen.Settings to Icons.Default.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        navItems.forEach { item ->
            if (item == null) {
                // Center FAB button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = onAddClick,
                        shape = RoundedCornerShape(14.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .size(56.dp)
                            .padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Session",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                val (screen, icon) = item
                val selected = screen == currentScreen

                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(screen) },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = screen.route,
                                tint = if (selected)
                                    MaterialTheme.colorScheme.primary // Icon color when selected
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant, // Icon color when not selected
                                modifier = Modifier.size(20.dp)
                            )
                            AnimatedContent(
                                targetState = selected,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab-label-animation"
                            ) { showLabel ->
                                if (showLabel) {
                                    Text(
                                        text = when (screen) {
                                            Screen.List -> "Timeline"
                                            Screen.Calendar -> "Calendar"
                                            Screen.Analytics -> "Stats"
                                            Screen.Settings -> "Settings"
                                            else -> ""
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface // Text color when unselected
                                    )
                                }
                            }
                        }
                    },
                    alwaysShowLabel = false,
                    modifier = Modifier.weight(1f)
                )

            }
        }
    }
}
