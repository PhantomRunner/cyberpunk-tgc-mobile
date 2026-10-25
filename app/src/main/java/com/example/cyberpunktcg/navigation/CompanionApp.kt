package com.example.cyberpunktcg.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun CompanionApp(isDarkTheme: Boolean, onDarkThemeChange: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentDestination = AppDestination.entries.firstOrNull {
        it.route == currentRoute
    } ?: AppDestination.Home

    Scaffold(
        topBar = {
            AppTopBar(
                titleResId = currentDestination.titleResId,
                showBackButton = currentDestination != currentDestination.mainTab,
                onBackClick = { navController.popBackStack() },
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedTab = currentDestination.mainTab,
                onTabSelected = { selectedTab ->
                    if (selectedTab != currentDestination.mainTab) {
                        navigateToMainTab(navController, selectedTab)
                    }
                },
            )
        },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            isDarkTheme = isDarkTheme,
            onDarkThemeChange = onDarkThemeChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}

private fun navigateToMainTab(navController: NavHostController, destination: AppDestination) {
    navController.navigate(destination.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
