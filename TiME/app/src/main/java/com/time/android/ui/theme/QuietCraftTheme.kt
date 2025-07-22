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

    // Color Palette for Categories
    val Violet = Color(0xFF7C4DFF)
    val Indigo = Color(0xFF536DFE)
    val Coral = Color(0xFFFF6F61)
    val Orange = Color(0xFFFF8F00)
    val SkyBlue = Color(0xFF00B0FF)
    val MintGreen = Color(0xFF00C853)

    val DeepPurple = Color(0xFF512DA8)
    val Teal = Color(0xFF009688)
    val RosePink = Color(0xFFEC407A)
    val Lime = Color(0xFFCDDC39)
    val Amber = Color(0xFFFFC107)
    val Cyan = Color(0xFF00ACC1)

    val White = Color(0xFFFFFFFF)
    val OffWhite = Color(0xFFFAFAFA)
    val LightSurface = Color(0xFFF5F5F5)
    val MediumSurface = Color(0xFFEEEEEE)
    val BorderGray = Color(0xFFE0E0E0)
    val DarkSurface = Color(0xFF212121)
    val TextDark = Color(0xFF1A1A1A)
    val TextLight = Color(0xFFBDBDBD)

    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFFC107)
    val Error = Color(0xFFF44336)
    val Info = Color(0xFF2196F3)
    val Disabled = Color(0xFF9E9E9E)
    val Focus = Color(0xFF82B1FF)

    val GlowViolet = Color(0xFFB388FF)
    val NeonGreen = Color(0xFF69F0AE)
    val ElectricBlue = Color(0xFF40C4FF)
    val PinkGlow = Color(0xFFF48FB1)
    val ShadowBlack = Color(0xFF000000)
    val Transparent = Color(0x00000000)

    val Ruby = Color(0xFFD32F2F)
    val Emerald = Color(0xFF2E7D32)
    val Sapphire = Color(0xFF1976D2)
    val Sand = Color(0xFFFFEBB7)
    val Midnight = Color(0xFF2C3E50)
    val Lavender = Color(0xFF9575CD)
    val Steel = Color(0xFF78909C)
    val Blush = Color(0xFFF8BBD0)
    val Mocha = Color(0xFF8D6E63)
    val Olive = Color(0xFF827717)
    val Ice = Color(0xFFE0F7FA)
    val Ash = Color(0xFFB0BEC5)
    val Carrot = Color(0xFFFF7043)
    val Plum = Color(0xFF9C27B0)
    val Azure = Color(0xFF03A9F4)
    val Banana = Color(0xFFFFF176)
    val Mint = Color(0xFF00E676)
    val Graphite = Color(0xFF37474F)
    val Charcoal = Color(0xFF263238)
    val CottonCandy = Color(0xFFFF80AB)
    val Fog = Color(0xFFECEFF1)
    val Peach = Color(0xFFFFAB91)
    val Periwinkle = Color(0xFF7986CB)
    val Clay = Color(0xFFBCAAA4)

    // === Category Color Mappings ===
    val CategoryColors = mapOf(
        "Work" to SkyBlue,
        "Fitness" to MintGreen,
        "Personal" to Sand,
        "Study" to Blush,
        "Other" to Lavender
    )


    //Color for ThemeManager
    // -- Color Presets --
    val ThemePresets = listOf(
        "Amber Glow" to Color(0xFFFFC107),
        "Coral Blush" to Color(0xFFFF6F61),
        "Terra Clay" to Color(0xFFD2691E),
        "Rose Dust" to Color(0xFFF48FB1),
        "Soft Peach" to Color(0xFFFFDAB9),
        "Sky Ice" to Color(0xFF81D4FA),
        "Oceanic" to Color(0xFF0288D1),
        "Teal Bloom" to Color(0xFF009688),
        "Frosted Lime" to Color(0xFFAED581),
        "Violet Dream" to Color(0xFF7C4DFF),
        "Midnight Purple" to Color(0xFF512DA8),
        "Lavender Fog" to Color(0xFFB39DDB),
        "Grape Fade" to Color(0xFF9575CD),
        "Orchid Pop" to Color(0xFFBA68C8),
        "Indigo Ink" to Color(0xFF3F51B5),
        "Denim" to Color(0xFF5C6BC0),
        "Sapphire Stone" to Color(0xFF1976D2),
        "Night Sky" to Color(0xFF283593),
        "Arctic Mist" to Color(0xFFE3F2FD),
        "Fresh Meadow" to Color(0xFF66BB6A),
        "Spruce Blue" to Color(0xFF33691E),
        "Forest Walk" to Color(0xFF388E3C),
        "Aloe Dew" to Color(0xFFC8E6C9),
        "Pale Sage" to Color(0xFFDCEDC8),
        "Graphite Gray" to Color(0xFF424242),
        "Ash Mist" to Color(0xFFBDBDBD),
        "Cream Paper" to Color(0xFFFFF8E1),
        "Dust White" to Color(0xFFF5F5F5),
        "Jet Black" to Color(0xFF212121),
        "Ink Blue" to Color(0xFF263238),
        "Neon Orange" to Color(0xFFFF5722),
        "Cyber Lime" to Color(0xFFCDDC39),
        "Pale Goldenrod" to Color(0xFFEEE8AA),
        "Medium Aquamarine" to Color(0xFF66CDAA),
        "Sky Blue" to Color(0xFF87CEEB),
        "Deep Sky Blue" to Color(0xFF00BFFF),
        "Plum" to Color(0xFFDDA0DD),
        "Mint Cream" to Color(0xFFF5FFFA),
        "Light Coral" to Color(0xFFF08080),
        "Gold" to Color(0xFFFFD700),
        "Silver" to Color(0xFFC0C0C0),
        "Light Sky Blue" to Color(0xFF87CEFA),
        "Honeydew" to Color(0xFFF0FFF0),
        "Aqua" to Color(0xFF00FFFF),
        "Dark Goldenrod" to Color(0xFFB8860B),
        "Crimson" to Color(0xFFDC143C),
        "Lemon Chiffon" to Color(0xFFFFFACD),
        "Coral" to Color(0xFFFF7F50),
        "Aquamarine" to Color(0xFF7FFFD4),
        "Goldenrod" to Color(0xFFDAA520),
        "Rose Quartz" to Color(0xFFF7CAC9),
        "Moonlight Lilac" to Color(0xFFE0BBE4),
        "Light Goldenrod Yellow" to Color(0xFFFAFAD2),
        "Azure" to Color(0xFFF0FFFF),
        "Teal" to Color(0xFF008080),
        "Misty Rose" to Color(0xFFFFE4E1)
    )

    val DefaultCategoryColor = Color(0xFFEDEDED)
    val CategoryColorPalette = CategoryColors.values.toSet()

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

    // === Main App Typography ===
    val AppTypography = Typography(
        displayLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        ),
        headlineMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        labelLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    )


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

