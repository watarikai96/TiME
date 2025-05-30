package com.time.app.ui.components.reusables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.time.app.model.Category
import com.time.app.ui.icons.IconRepository
import com.time.app.ui.theme.QuietCraftTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryIconSelector(
    categories: List<Category>,
    selectedCategory: Category?,
    selectedIconName: String,
    onCategorySelected: (Category) -> Unit,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier


) {
    var showIconPicker by remember { mutableStateOf(false) }
    val iconTint = selectedCategory?.color?.toInt()?.let { Color(it) }
        ?: MaterialTheme.colorScheme.onSurfaceVariant


    QuietCraftTheme.QuietCraftCard(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // SECTION 1: CATEGORY SELECTION (now completely independent)
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Category chips - horizontal scroll
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory?.id == category.id
                    val categoryColor = Color(category.color.toInt())

                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCategorySelected(category) }
                            .background(
                                if (isSelected) categoryColor.copy(alpha = 0.9f)
                                else categoryColor.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (isSelected) categoryColor
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = category.name.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 0.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                                        else categoryColor.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                }
            }

            // SECTION 2: ICON SELECTION (now completely independent)
            Text(
                text = "ICON",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showIconPicker = true },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon display - no longer uses category color
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                iconTint.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageVector = IconRepository.getIconByName(selectedIconName)
                        if (imageVector != null) {
                            Icon(
                                imageVector = imageVector,
                                contentDescription = selectedIconName,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            IconRepository.RenderIcon(
                                name = selectedIconName,
                                color = iconTint,
                                fontSizeSp = 16,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "SELECTED ICON",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                        val formattedIconName = IconRepository
                            .getAllIconNames()
                            .find { it == selectedIconName }
                            ?.replaceFirstChar { it.uppercase() }
                            ?: "Select an Icon"

                        Text(
                            formattedIconName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )

                    }

                    Icon(
                        Icons.Outlined.ChevronRight,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Icon picker modal (unchanged)
    if (showIconPicker) {
        ModalBottomSheet(
            onDismissRequest = { showIconPicker = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = QuietCraftTheme.cardBackground
        ) {
            var searchQuery by remember { mutableStateOf("") }

            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    placeholder = { Text("Search icons...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(
                        items = IconRepository.getAllIconNames()
                            .filter { it.contains(searchQuery, ignoreCase = true) }
                            .sorted()
                    ) { iconName ->
                        IconButton(
                            onClick = {
                                onIconSelected(iconName)
                                showIconPicker = false
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val imageVector = IconRepository.getIconByName(iconName)
                                if (imageVector != null) {
                                    Icon(
                                        imageVector = imageVector,
                                        contentDescription = iconName,
                                        tint = iconTint,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    IconRepository.RenderIcon(
                                        name = iconName,
                                        color = iconTint,
                                        fontSizeSp = 20,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}