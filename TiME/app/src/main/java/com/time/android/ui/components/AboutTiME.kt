package com.time.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutTiMEModal(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 4.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "TiME",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Track  ·  Improve  ·  Manage  ·  EXECUTE",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontStyle = FontStyle.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(0.5f),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )

// Signature Summary
            Text(
                text = "A refined timeboxing companion crafted for modern minds. " +
                        "TiME helps you focus, reflect, and execute with clarity — blending structured flow with a minimalist soul.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

// Footer and Credits

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Crafted with ❤️ by QuietCraft™
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crafted with",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Cursive,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Icon(
                        imageVector = Icons.TwoTone.Favorite,
                        contentDescription = "Love",
                        tint = Color.Red.copy(alpha = 0.85f),
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(20.dp)
                    )
                    Text(
                        text = "by",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Cursive,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "QuietCraft™",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Powered by HyperFocus™
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Powered by ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.3.sp
                        )
                    )
                    Text(
                        text = "HyperFocus™",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // A TiME Technology
                Text(
                    text = "A TiME Technology",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Credits
                Text(
                    text = "Credits",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                )

                Text(
                    text = "Flag & UI Icons by Geekclick, Freepik and Smashicons (Flaticon)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(Modifier.height(16.dp))
            }

        }
    }
}