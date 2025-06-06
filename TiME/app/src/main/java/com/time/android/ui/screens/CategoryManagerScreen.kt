@file:OptIn(ExperimentalMaterial3Api::class)

package com.time.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.time.android.model.Category
import com.time.android.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerScreen(
    categoryViewModel: CategoryViewModel = viewModel(),
    isModalOpen: Boolean,
    onDismiss: () -> Unit
) {
    val categories by categoryViewModel.categories.collectAsState()
    var newCategoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(0xFFE57373L) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isModalOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Text(
                    text = "NEW CATEGORY",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Divider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                // Category Name
                Text(
                    text = "NAME",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("Enter category name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Color selection
                Text(
                    text = "SELECT COLOR",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                //  Color palette
                val presetColors = listOf(
                    // Reds/Pinks
                    0xFFEF4444L, 0xFFDC2626L, 0xFFF43F5EL, 0xFFE11D48L,
                    // Oranges
                    0xFFF97316L, 0xFFEA580CL, 0xFFFF7849L, 0xFFFDBA74L,
                    // Yellows
                    0xFFFACC15L, 0xFFEAB308L, 0xFFFFD700L, 0xFFFDE047L,
                    // Greens
                    0xFF22C55EL, 0xFF16A34AL, 0xFF84CC16L, 0xFF4ADE80L,
                    // Teals/Blues
                    0xFF06B6D4L, 0xFF0891B2L, 0xFF3B82F6L, 0xFF2563EBL,
                    // Purples/Violets
                    0xFF8B5CF6L, 0xFF7C3AEDL, 0xFFD946EFL, 0xFFC026D3L,
                    // Stylish extras
                    0xFF64748BL, 0xFF475569L, 0xFFF472B6L, 0xFFDB2777L,
                    // Neutrals
                    0xFFA8A29EL, 0xFF78716CL, 0xFF57534EL, 0xFF44403CL,
                    // Additional colors for full 56-color palette
                    0xFFEC4899L, 0xFFBE185DL, 0xFFF59E0BL, 0xFFD97706L,
                    0xFF10B981L, 0xFF059669L, 0xFF0EA5E9L, 0xFF0284C7L,
                    0xFF6366F1L, 0xFF4F46E5L, 0xFF9333EAL, 0xFF7E22CEL,
                    0xFFF43F5EL, 0xFFE11D48L, 0xFFF97316L, 0xFFEA580CL,
                    0xFF84CC16L, 0xFF65A30DL, 0xFF22D3EEL, 0xFF06B6D4L,
                    0xFF8B5CF6L, 0xFF7C3AEDL, 0xFFEC4899L, 0xFFDB2777L
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { colorLong ->
                        val color = Color(colorLong)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color = color, shape = CircleShape)
                                .clickable { selectedColor = colorLong }
                                .border(
                                    width = if (selectedColor == colorLong) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colorLong) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                // Existing Categories
                Text(
                    text = "YOUR CATEGORIES",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (categories.isEmpty()) {
                        Text(
                            text = "No categories yet",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        categories.forEach { category ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* Edit functionality */ },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 0.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(
                                                    color = Color(category.color),
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = category.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(
                                        onClick = { categoryViewModel.deleteCategory(category.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

// Create Button
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            categoryViewModel.addCategory(Category(name = newCategoryName, color = selectedColor))
                            newCategoryName = ""
                            selectedColor = 0xFFE57373L
                            onDismiss()
                        }
                    },
                    enabled = newCategoryName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        text = "CREATE CATEGORY",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (newCategoryName.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
    }
}
