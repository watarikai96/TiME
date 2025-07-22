package com.time.android.ui.theme

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
import com.time.android.ui.theme.QuietCraftTheme.ThemePresets

// -- DataStore Setup --
private val Context.dataStore by preferencesDataStore("time_theme")
private val ACCENT_COLOR_KEY = stringPreferencesKey("accent_color")
private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

// -- Enum for Theme Mode --
enum class ThemeMode { Light, Dark, System }

fun getAccentColorName(color: Color): String {
    return ThemePresets.find { it.second.value == color.value }?.first
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
        typography = QuietCraftTheme.AppTypography,
        content = content
    )
}