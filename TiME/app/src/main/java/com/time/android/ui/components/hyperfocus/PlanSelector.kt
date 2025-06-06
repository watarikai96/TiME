package com.time.android.ui.components.hyperfocus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.twotone.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.time.android.model.HyperFocusPlanProfile
import com.time.android.ui.theme.QuietCraftTheme
import com.time.android.ui.theme.QuietCraftTheme.QuietCraftCard
import com.time.android.ui.theme.QuietCraftTheme.QuietCraftFilledButton
import com.time.android.ui.theme.QuietCraftTheme.QuietCraftOutlinedButton
import com.time.android.ui.theme.QuietCraftTheme.QuietCraftTextButton
import com.time.android.viewmodel.HyperFocusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanSelector(
    viewModel: HyperFocusViewModel,
    onCreateNewPlan: () -> Unit,
    onPlanSelected: (HyperFocusPlanProfile) -> Unit
) {
    val plans = viewModel.savedPlans
    val scrollState = rememberScrollState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Box(
        modifier = Modifier
            .heightIn(max = screenHeight * 0.75f)
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(QuietCraftTheme.Spacing.large)
        ) {
            QuietCraftTheme.QuietCraftSectionHeader(
                icon = Icons.TwoTone.Bolt,
                title = "Saved HyperFocus Plans"
            )

            if (plans.isEmpty()) {
                QuietCraftCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(QuietCraftTheme.Spacing.medium)
                    ) {
                        Text(
                            "No saved plans yet",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        QuietCraftTextButton(
                            label = "Create Your First Plan",
                            onClick = onCreateNewPlan
                        )
                    }
                }
            } else {
                plans.forEach { plan ->
                    QuietCraftCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(QuietCraftTheme.Spacing.medium)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = plan.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = "Created ${
                                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                                .format(Date(plan.createdAt))
                                        }",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deletePlan(plan) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete Plan",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            QuietCraftFilledButton(
                                label = "Start Plan",
                                onClick = {
                                    viewModel.prepareQueueFromPlan(plan)
                                    onPlanSelected(plan)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            QuietCraftOutlinedButton(
                label = "Create New Plan",
                onClick = onCreateNewPlan,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
