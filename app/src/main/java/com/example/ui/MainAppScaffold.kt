package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AddHabitScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BrightLilac
import com.example.ui.theme.DeepVioletSurface
import com.example.ui.theme.MidnightPurple
import com.example.ui.theme.RoyalPurpleElevated
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.VividPurple
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainAppScaffold(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to user messages for Snackbars
    LaunchedEffect(viewModel) {
        viewModel.userMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // MANDATORY RTL Interface across the entire application
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MidnightPurple,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = DeepVioletSurface,
                    contentColor = TextPrimaryLight,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav")
                ) {
                    // Item 1: Dashboard
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.DASHBOARD) {
                                    Icons.Filled.CheckCircle
                                } else {
                                    Icons.Outlined.CheckCircleOutline
                                },
                                contentDescription = "عاداتي"
                            )
                        },
                        label = {
                            Text(
                                text = "عاداتي",
                                fontWeight = if (currentScreen == AppScreen.DASHBOARD) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimaryLight,
                            selectedTextColor = BrightLilac,
                            indicatorColor = VividPurple,
                            unselectedIconColor = TextMutedLight,
                            unselectedTextColor = TextMutedLight
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    // Item 2: Add Habit
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.ADD_HABIT,
                        onClick = { viewModel.navigateTo(AppScreen.ADD_HABIT) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.ADD_HABIT) {
                                    Icons.Filled.AddCircle
                                } else {
                                    Icons.Outlined.AddCircleOutline
                                },
                                contentDescription = "إضافة عادة"
                            )
                        },
                        label = {
                            Text(
                                text = "إضافة عادة",
                                fontWeight = if (currentScreen == AppScreen.ADD_HABIT) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimaryLight,
                            selectedTextColor = BrightLilac,
                            indicatorColor = VividPurple,
                            unselectedIconColor = TextMutedLight,
                            unselectedTextColor = TextMutedLight
                        ),
                        modifier = Modifier.testTag("nav_add_habit")
                    )

                    // Item 3: Settings
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.SETTINGS) {
                                    Icons.Filled.Settings
                                } else {
                                    Icons.Outlined.Settings
                                },
                                contentDescription = "الإعدادات"
                            )
                        },
                        label = {
                            Text(
                                text = "الإعدادات",
                                fontWeight = if (currentScreen == AppScreen.SETTINGS) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimaryLight,
                            selectedTextColor = BrightLilac,
                            indicatorColor = VividPurple,
                            unselectedIconColor = TextMutedLight,
                            unselectedTextColor = TextMutedLight
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MidnightPurple)
            ) {
                Crossfade(
                    targetState = currentScreen,
                    animationSpec = tween(250),
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        AppScreen.DASHBOARD -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToAdd = { viewModel.navigateTo(AppScreen.ADD_HABIT) }
                            )
                        }

                        AppScreen.ADD_HABIT -> {
                            AddHabitScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                            )
                        }

                        AppScreen.SETTINGS -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                            )
                        }
                    }
                }
            }
        }
    }
}
