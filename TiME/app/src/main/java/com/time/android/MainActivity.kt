package com.time.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.time.android.navigation.AppNavGraph
import com.time.android.navigation.Screen
import com.time.android.ui.components.timeline.AnimatedClockLoader
import com.time.android.ui.components.timeline.BottomNavBar
import com.time.android.ui.screens.AddTiMEScreen
import com.time.android.ui.theme.ThemeManager
import com.time.android.ui.theme.ThemeMode
import com.time.android.ui.theme.TiMETheme
import com.time.android.viewmodel.CategoryViewModel
import com.time.android.viewmodel.HyperFocusViewModel
import com.time.android.viewmodel.TiMEViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val hyperFocusViewModel: HyperFocusViewModel = viewModel()
            val timeViewModel: TiMEViewModel = viewModel()
            val categoryViewModel: CategoryViewModel = viewModel()
                val showAddModal = rememberSaveable { mutableStateOf(false) }
            val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            // Theme Config
            val themeMode by ThemeManager.themeModeFlow(context).collectAsState(initial = ThemeMode.System)
            val accentColor by ThemeManager.accentColorFlow(context).collectAsState(initial = Color(0xFF7C4DFF))

            val useDarkTheme = when (themeMode) {
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.System -> isSystemInDarkTheme()
            }

            LaunchedEffect(Unit) {
                hyperFocusViewModel.injectDataStore(context)
                hyperFocusViewModel.restorePersistedState(context)
            }



            // Animated Splash Screen ( Clock Loader )
            var showMainApp by remember { mutableStateOf(false) }

            TiMETheme(useDarkTheme = useDarkTheme, accentColor = accentColor) {
                if (!showMainApp) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedClockLoader(
                            size = 200.dp,
                            color = accentColor,
                            secondColor = MaterialTheme.colorScheme.secondary
                        )
                    }

                    LaunchedEffect(Unit) {
                        delay(3000L)
                        showMainApp = true
                    }
                } else {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val currentScreen = remember(currentRoute) {
                        Screen.fromRoute(currentRoute)
                    }

                    Scaffold(
                        bottomBar = {
                            BottomNavBar(
                                currentScreen = currentScreen,
                                onTabSelected = { screen ->
                                    if (screen.route != currentRoute) {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.List.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                onAddClick = { showAddModal.value = true }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            //  Navigation
                            AppNavGraph(
                                navController = navController,
                                viewModel = timeViewModel,
                                categoryViewModel = categoryViewModel,
                                hyperFocusViewModel = hyperFocusViewModel
                            )

                            //  Add Session Modal
                            if (showAddModal.value) {
                                ModalBottomSheet(
                                    onDismissRequest = { showAddModal.value = false },
                                    sheetState = bottomSheetState,
                                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                                    tonalElevation = 4.dp
                                ) {
                                    AddTiMEScreen(
                                        navController = navController,
                                        viewModel = timeViewModel,
                                        hyperFocusViewModel = hyperFocusViewModel,
                                        categoryViewModel = categoryViewModel,
                                        onDone = { showAddModal.value = false }
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