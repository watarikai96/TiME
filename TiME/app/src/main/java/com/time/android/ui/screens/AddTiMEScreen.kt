@file:OptIn(ExperimentalMaterial3Api::class)
package com.time.android.ui.screens

import com.time.android.viewmodel.HyperFocusViewModel
import com.time.android.ui.components.hyperfocus.PlanSelector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.Notes
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Note
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.time.android.model.Category
import com.time.android.model.TiME
import com.time.android.ui.components.hyperfocus.NewPlanCreatorModal
import com.time.android.ui.components.reusables.CategoryIconSelector
import com.time.android.ui.components.reusables.TimeRangeCard
import com.time.android.ui.theme.QuietCraftTheme
import com.time.android.viewmodel.CategoryViewModel
import com.time.android.viewmodel.TiMEViewModel
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTiMEScreen(
    navController: NavHostController,
    viewModel: TiMEViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    hyperFocusViewModel: HyperFocusViewModel = viewModel(),
    onDone: () -> Unit
) {
    val categories by categoryViewModel.categories.collectAsState()
    val scrollState = rememberScrollState()
    LocalFocusManager.current


    val isHyperFocusMode = hyperFocusViewModel.isHyperFocusMode

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableLongStateOf(startTime + 3600000) }
    var isBreak by remember { mutableStateOf(false) }
    var repeatOption by remember { mutableStateOf("Once") }
    var autoMove by remember { mutableStateOf(false) }
    var selectedIconName by remember { mutableStateOf("Star") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var hasUserChosenIcon by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var tagInput by remember { mutableStateOf("") }

    val accentColor = MaterialTheme.colorScheme.primary
    val containerColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.surfaceContainerHighest

    val isValid = title.isNotBlank() && selectedCategory != null && endTime > startTime
    val dayOffset = selectedDate.timeInMillis - Calendar.getInstance().timeInMillis

    LaunchedEffect(Unit) {
        hyperFocusViewModel.loadSavedPlans()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Session", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (!isHyperFocusMode.value) {
                        TextButton(
                            onClick = {
                                if (isValid) {
                                    viewModel.add(
                                        TiME(
                                            title = title,
                                            description = description,
                                            category = selectedCategory?.id.orEmpty(),
                                            categoryName = selectedCategory?.name.orEmpty(),
                                            startTime = startTime + dayOffset,
                                            endTime = endTime + dayOffset,
                                            isBreak = isBreak,
                                            iconName = selectedIconName,
                                            repeat = repeatOption,
                                            autoMove = autoMove,
                                            tags = tags,
                                            notes = ""
                                        )
                                    )
                                    onDone()
                                }
                            },
                            enabled = isValid,
                            colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
                        ) {
                            Text("SAVE", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        },
        containerColor = containerColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // HyperFocus Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable HyperFocus", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isHyperFocusMode.value,
                        onCheckedChange = { isHyperFocusMode.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = accentColor,
                            checkedTrackColor = accentColor.copy(alpha = 0.5f)
                        )
                    )
                }

                if (isHyperFocusMode.value) {
                    PlanSelector(
                        viewModel = hyperFocusViewModel,
                        onCreateNewPlan = { hyperFocusViewModel.isAddModalOpen.value = true },
                        onPlanSelected = { plan ->
                            hyperFocusViewModel.prepareQueueFromPlan(plan)
                            onDone()
                        }
                    )
                } else {
                    // Title Section
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        BasicTextField(
                            value = title,
                            onValueChange = { title = it },
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal
                            ),
                            decorationBox = { innerTextField ->
                                if (title.isEmpty()) {
                                    Text(
                                        "What are you focusing on?",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.4f
                                            )
                                        )
                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            cursorBrush = SolidColor(accentColor)
                        )

                        Divider(
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            thickness = 1.dp
                        )
                    }

                    //Description
                    if (!isBreak) {
                        QuietCraftTheme.QuietCraftTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = "Describe your Session",
                            leadingIcon = Icons.AutoMirrored.TwoTone.Notes,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }

                    // Main Form Content
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Time Selection Card
                        TimeRangeCard(
                            selectedDate = selectedDate,
                            startTime = startTime,
                            endTime = endTime,
                            onDateTimeChange = { date, start, end ->
                                selectedDate = date
                                startTime = start
                                endTime = end
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Category and Icon Selector
                        CategoryIconSelector(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            selectedIconName = selectedIconName,
                            onCategorySelected = { category ->
                                selectedCategory = category
                                // Auto-select first icon if category changes
                                if (!hasUserChosenIcon) {
                                    selectedIconName =
                                        category.id // only auto-assign if user hasn't picked an icon
                                }
                            },
                            onIconSelected = { iconName ->
                                selectedIconName = iconName
                                hasUserChosenIcon = true
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Additional Options
                        if (!isBreak) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                QuietCraftTheme.QuietCraftLabel("Repeat")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "Once",
                                        "Daily",
                                        "Weekly",
                                        "Monthly"
                                    ).forEach { option ->
                                        QuietCraftTheme.QuietCraftChip(
                                            selected = repeatOption == option,
                                            onClick = { repeatOption = option },
                                            label = {
                                                Text(
                                                    option,
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        //Tags
                        if (!isBreak) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                QuietCraftTheme.QuietCraftLabel("Tags")

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    tags.forEach { tag ->
                                        AssistChip(
                                            onClick = { tags = tags - tag },
                                            label = { Text(tag) },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                labelColor = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }

                                QuietCraftTheme.QuietCraftTextField(
                                    value = tagInput,
                                    onValueChange = { tagInput = it },
                                    placeholder = "Add Tag",
                                    leadingIcon = null,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (tagInput.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            if (tagInput.isNotBlank() && tagInput !in tags) {
                                                tags += tagInput.trim()
                                                tagInput = ""
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(
                                            Icons.TwoTone.Check,
                                            contentDescription = "Add Tag"
                                        )
                                    }
                                }
                            }
                        }

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = cardColor
                            )
                        ) {
                            Column {
                                ListItem(
                                    headlineContent = { Text("Options") },
                                    leadingContent = {
                                        Icon(
                                            Icons.Outlined.Tune,
                                            null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )

                                // Break Toggle
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .height(48.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Coffee,
                                        null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        "This is a break",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Switch(
                                        checked = isBreak,
                                        onCheckedChange = { isBreak = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = accentColor,
                                            checkedTrackColor = accentColor.copy(alpha = 0.5f)
                                        )
                                    )
                                }

                                // Auto-move Toggle
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .height(48.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Repeat,
                                        null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        "Auto-move unfinished",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Switch(
                                        checked = autoMove,
                                        onCheckedChange = { autoMove = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = accentColor,
                                            checkedTrackColor = accentColor.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                            }
                        }

                        // Description
                        if (!isBreak) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = cardColor
                                )
                            ) {
                                Column {
                                    ListItem(
                                        headlineContent = { Text("Notes") },
                                        leadingContent = {
                                            Icon(
                                                Icons.TwoTone.Note,
                                                null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    )

                                    TextField(
                                        value = description,
                                        onValueChange = { description = it },
                                        placeholder = { Text("Add details...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        textStyle = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }

            //  Plan Creator Modal
            if (hyperFocusViewModel.isAddModalOpen.value) {
                NewPlanCreatorModal(
                    viewModel = hyperFocusViewModel,
                    categories = categories,
                    onDismiss = { hyperFocusViewModel.isAddModalOpen.value = false }
                )
            }
        }
    }
}
