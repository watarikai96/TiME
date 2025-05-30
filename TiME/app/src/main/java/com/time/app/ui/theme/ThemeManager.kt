package com.time.app.ui.theme

import android.content.Context
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// -- DataStore Setup --
private val Context.dataStore by preferencesDataStore("time_theme")
private val ACCENT_COLOR_KEY = stringPreferencesKey("accent_color")
private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

// -- Enum for Theme Mode --
enum class ThemeMode { Light, Dark, System }

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

fun getAccentColorName(color: Color): String {
    return ThemePresets.find { it.second.value.toULong() == color.value.toULong() }?.first
        ?: "Custom"
}


// -- Central Store Object --
object ThemeManager {

    suspend fun setAccentColor(context: Context, color: Color) {
        context.dataStore.edit { prefs ->
            prefs[ACCENT_COLOR_KEY] = color.value.toString()
        }
    }

    fun accentColorFlow(context: Context): Flow<Color> =
        context.dataStore.data.map { prefs ->
            prefs[ACCENT_COLOR_KEY]?.toULongOrNull()?.let { Color(it) } ?: Color(0xFF7C4DFF)
        }

    suspend fun setThemeMode(context: Context, mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
        }
    }

    fun themeModeFlow(context: Context): Flow<ThemeMode> =
        context.dataStore.data.map { prefs ->
            when (prefs[THEME_MODE_KEY]) {
                "Light" -> ThemeMode.Light
                "Dark" -> ThemeMode.Dark
                else -> ThemeMode.System
            }
        }

    @Composable
    fun accentColor(): Color {
        val context = LocalContext.current
        return accentColorFlow(context).collectAsState(initial = Color(0xFF7C4DFF)).value
    }


    @Composable
    fun buttonColors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = accentColor(),
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

}
@Composable
fun TiMETheme(
    useDarkTheme: Boolean,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    LaunchedEffect(accentColor) {
        Log.d("TiMETheme", "Accent color now: ${accentColor.toArgb().toString(16)}")
    }

    val colors = if (useDarkTheme) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = accentColor.copy(alpha = 0.2f),
            secondary = accentColor,
            background = Color(0xFF121212),
            onBackground = Color.White,
            surface = Color(0xFF1A1C1E),
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = accentColor.copy(alpha = 0.2f),
            secondary = accentColor,
            background = Color(0xFFFDFBFF),
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}