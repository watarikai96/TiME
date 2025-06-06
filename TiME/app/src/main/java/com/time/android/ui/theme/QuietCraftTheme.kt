package com.time.android.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

object QuietCraftTheme {

    // === Color Palette ===
    val cardBackground: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerHighest
    val chipBackground: Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val selectedChipBackground: Color @Composable get() = MaterialTheme.colorScheme.primaryContainer
    val dividerColor: Color @Composable get() = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    val badgeColor: Color @Composable get() = MaterialTheme.colorScheme.secondaryContainer
    val subtleHighlight: Color @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

    // === Spacing Constants ===
    object Spacing {
        val small = 8.dp
        val medium = 16.dp
        val large = 24.dp
    }

    // === Typography ===
    val headlineStyle: TextStyle @Composable get() = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.2).sp
    )

    val sectionHeaderStyle: TextStyle @Composable get() = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
        letterSpacing = 0.2.sp
    )

    val subtleLabelStyle: TextStyle @Composable get() = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp
    )

    val inputTextStyle: TextStyle @Composable get() = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Normal
    )

    // === Card ===
    @Composable
    fun QuietCraftCard(
        modifier: Modifier = Modifier,
        shape: Shape = RoundedCornerShape(16.dp),
        content: @Composable ColumnScope.() -> Unit
    ) {
        ElevatedCard(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.elevatedCardColors(containerColor = cardBackground),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
        ) {
            content()
        }
    }

    // === Chips ===
    @Composable
    fun QuietCraftChip(
        selected: Boolean,
        onClick: () -> Unit,
        leadingIcon: @Composable (() -> Unit)? = null,
        label: @Composable () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .animateContentSize(),
            color = if (selected) selectedChipBackground else chipBackground
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.invoke()
                if (leadingIcon != null) Spacer(modifier = Modifier.width(6.dp))
                label()
            }
        }
    }

    // === Section Header ===
    @Composable
    fun QuietCraftSectionHeader(
        icon: ImageVector,
        title: String,
        modifier: Modifier = Modifier
    ) {
        ListItem(
            modifier = modifier,
            headlineContent = { Text(title, style = sectionHeaderStyle) },
            leadingContent = {
                Icon(
                    icon,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }

    // === Text Field ===
    @Composable
    fun QuietCraftTextField(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        modifier: Modifier = Modifier,
        leadingIcon: ImageVector? = null,
        isError: Boolean = false
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, style = subtleLabelStyle) },
            modifier = modifier,
            singleLine = true,
            textStyle = inputTextStyle,
            isError = isError,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                errorIndicatorColor = MaterialTheme.colorScheme.error
            ),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // === Badges & Labels ===
    @Composable
    fun QuietCraftBadge(text: String, modifier: Modifier = Modifier) {
        Surface(
            color = badgeColor,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 2.dp,
            modifier = modifier
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }

    @Composable
    fun QuietCraftLabel(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = text.uppercase(),
            style = subtleLabelStyle,
            color = color,
            modifier = modifier
        )
    }

    // === Buttons ===
    @Composable
    fun QuietCraftFilledButton(
        onClick: () -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }



    @Composable
    fun QuietCraftOutlinedButton(
        onClick: () -> Unit,
        label: String,
        modifier: Modifier = Modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = modifier
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }

    @Composable
    fun QuietCraftTextButton(
        onClick: () -> Unit,
        label: String,
        modifier: Modifier = Modifier
    ) {
        TextButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }

    // === Utility Primitives ===
    @Composable
    fun QuietCraftDivider(modifier: Modifier = Modifier) {
        Divider(color = dividerColor, thickness = 1.dp, modifier = modifier)
    }

    @Composable
    fun QuietCraftSpacer(height: Dp = 16.dp) {
        Spacer(modifier = Modifier.height(height))
    }

    @Composable
    fun QuietCraftIconBox(
        icon: ImageVector,
        background: Color = subtleHighlight,
        tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier: Modifier = Modifier.size(40.dp)
    ) {
        Box(
            modifier = modifier
                .background(background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        }
    }

    @Composable
    fun QuietCraftAnimatedContent(
        visible: Boolean,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(250)) + expandVertically(),
            exit = fadeOut(tween(250)) + shrinkVertically()
        ) {
            content()
        }
    }
}

@Composable
fun QuietCraftTheme.QuietCraftDurationChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = chipBackground
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

//SETTING CARD
@Composable
fun QuietCraftTheme.QuietCraftSettingCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 0.dp, vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            trailingContent?.invoke()
        }
    }
}


//Minimal Setting Card
@Composable
fun QuietCraftTheme.QuietCraftSettingCardMinimal(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current // ✔️ this uses Material3’s correct ripple
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            trailingContent?.invoke()
        }
    }
}



//FILLED BUTTON
@Composable
fun QuietCraftTheme.QuietCraftFilledButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


//Minimal Filled Button
@Composable
fun QuietCraftTheme.QuietCraftFilledButtonMinimal(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true,
    elevation: Dp = 2.dp
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.38f),
            disabledContentColor = contentColor.copy(alpha = 0.38f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = elevation + 2.dp,
            disabledElevation = 0.dp
        ),
        enabled = enabled
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        )
    }
}


//OUTLINED BUTTON
@Composable
fun QuietCraftTheme.QuietCraftOutlinedButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

//OUTLINED BUTTON MINIMAL
@Composable
fun QuietCraftTheme.QuietCraftOutlinedButtonMinimal(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) borderColor else borderColor.copy(alpha = 0.38f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.12f),
            disabledContentColor = contentColor.copy(alpha = 0.38f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        )
    }
}


//TEXT BUTTON
@Composable
fun QuietCraftTheme.QuietCraftTextButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}



// TEXT BUTTON (Minimal, soft-padded, tactile)
@Composable
fun QuietCraftTheme.QuietCraftTextButtonMinimal(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}


//Setting Toggle

@Composable
fun QuietCraftTheme.QuietCraftSettingToggleButton(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = { onToggle(!checked) },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier.fillMaxWidth(),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 1.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    }
}



//Settings Action Button

@Composable
fun QuietCraftTheme.QuietCraftSettingActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingPreview: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth(),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 1.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        if (trailingPreview != null) {
            Spacer(Modifier.width(8.dp))
            trailingPreview()
        }
    }
}

//Sync Button
@Composable
fun QuietCraftTheme.QuietCraftSyncButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 1.dp)
    ) {
        Icon(
            Icons.TwoTone.Sync,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text("Sync Now", style = MaterialTheme.typography.labelLarge)
    }
}


//About Button
@Composable
fun QuietCraftTheme.QuietCraftAboutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    ) {
        Icon(
            Icons.TwoTone.Info,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("About TiME", style = MaterialTheme.typography.labelLarge)
    }
}



//SignOut Button

@Composable
fun QuietCraftTheme.QuietCraftSignOutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
        )
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text("Sign Out", style = MaterialTheme.typography.labelLarge)
    }
}

//Elevated Button

@Composable
fun QuietCraftTheme.QuietCraftElevatedButton(
    label: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 1.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

