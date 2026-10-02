package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.HabitViewModel
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.ArabicStrings
import com.example.ui.localization.EnglishStrings
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.screens.AddHabitScreen
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: HabitViewModel = viewModel()
            val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

            val strings = if (appLanguage == AppLanguage.ARABIC) ArabicStrings else EnglishStrings
            val layoutDirection = if (appLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            // Permission Request for Notifications (Android 13+)
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Permission granted or denied handled gracefully */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            CompositionLocalProvider(
                LocalLayoutDirection provides layoutDirection,
                LocalAppLanguage provides appLanguage,
                LocalAppStrings provides strings
            ) {
                MyApplicationTheme(themeSettings = themeSettings) {
                    HabitTrackerApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HabitTrackerApp(viewModel: HabitViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val strings = if (LocalAppLanguage.current == AppLanguage.ARABIC) ArabicStrings else EnglishStrings
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Global back handler to return to Dashboard if on a sub-screen
    if (currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentScreen != AppScreen.ADD_HABIT) {
                NavigationBar(
                    containerColor = AppTheme.colors.surface,
                    contentColor = AppTheme.colors.textPrimary,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = strings.navDashboard
                            )
                        },
                        label = { Text(strings.navDashboard) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppTheme.colors.primary,
                            selectedTextColor = AppTheme.colors.primary,
                            indicatorColor = AppTheme.colors.primary.copy(alpha = 0.15f),
                            unselectedIconColor = AppTheme.colors.textSecondary,
                            unselectedTextColor = AppTheme.colors.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.STATISTICS,
                        onClick = { viewModel.navigateTo(AppScreen.STATISTICS) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = strings.navStats
                            )
                        },
                        label = { Text(strings.navStats) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppTheme.colors.primary,
                            selectedTextColor = AppTheme.colors.primary,
                            indicatorColor = AppTheme.colors.primary.copy(alpha = 0.15f),
                            unselectedIconColor = AppTheme.colors.textSecondary,
                            unselectedTextColor = AppTheme.colors.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_stats")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.AI_COACH,
                        onClick = { viewModel.navigateTo(AppScreen.AI_COACH) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = strings.navAiCoach
                            )
                        },
                        label = { Text(strings.navAiCoach) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppTheme.colors.primary,
                            selectedTextColor = AppTheme.colors.primary,
                            indicatorColor = AppTheme.colors.primary.copy(alpha = 0.15f),
                            unselectedIconColor = AppTheme.colors.textSecondary,
                            unselectedTextColor = AppTheme.colors.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_ai_coach")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = strings.navSettings
                            )
                        },
                        label = { Text(strings.navSettings) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppTheme.colors.primary,
                            selectedTextColor = AppTheme.colors.primary,
                            indicatorColor = AppTheme.colors.primary.copy(alpha = 0.15f),
                            unselectedIconColor = AppTheme.colors.textSecondary,
                            unselectedTextColor = AppTheme.colors.textSecondary
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = { viewModel.navigateTo(AppScreen.ADD_HABIT) }
                )
                AppScreen.STATISTICS -> StatisticsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                )
                AppScreen.AI_COACH -> AiCoachScreen(
                    viewModel = viewModel
                )
                AppScreen.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                )
                AppScreen.ADD_HABIT -> AddHabitScreen(
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                )
            }
        }
    }
}
