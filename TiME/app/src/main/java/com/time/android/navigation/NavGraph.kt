package com.time.android.navigation

import com.time.android.viewmodel.HyperFocusViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.time.android.ui.components.TimeFormatOption
import com.time.android.ui.screens.AnalyticsScreen
import com.time.android.ui.screens.CalendarViewScreen
import com.time.android.ui.screens.SettingsScreen
import com.time.android.ui.screens.TimelineScreen
import com.time.android.viewmodel.CategoryViewModel
import com.time.android.viewmodel.TiMEViewModel


sealed class Screen(val route: String) {
    object List : Screen("time_list")
    object Calendar : Screen("calendar_view")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")

    companion object {
        fun fromRoute(route: String?): Screen = when (route) {
            List.route -> List
            Calendar.route -> Calendar
            Analytics.route -> Analytics
            Settings.route -> Settings
            else -> List
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: TiMEViewModel,
    categoryViewModel: CategoryViewModel,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
    hyperFocusViewModel: HyperFocusViewModel

) {
    var selectedFormat by rememberSaveable { mutableStateOf(TimeFormatOption.HOURS_MINUTES) }

    NavHost(
        navController = navController,
        startDestination = Screen.List.route,
        modifier = modifier
    ) {
        composable(Screen.List.route) {
            TimelineScreen(
                viewModel = viewModel,
                categoryViewModel = categoryViewModel,
                selectedFormat = selectedFormat,
                navController = navController,
                hyperFocusViewModel = hyperFocusViewModel
            )

        }

        composable(Screen.Calendar.route) {
            CalendarViewScreen(selectedFormat = selectedFormat)
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                viewModel = viewModel,
                categoryViewModel = categoryViewModel,
                selectedFormat = selectedFormat
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onSignOut = onSignOut
            )

        }
    }
}

