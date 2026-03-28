package com.bear.asset.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bear.asset.data.local.TokenManager
import com.bear.asset.ui.auth.LoginScreen
import com.bear.asset.ui.auth.LoginViewModel
import com.bear.asset.ui.screen.AiScreen
import com.bear.asset.ui.screen.AssetScreen
import com.bear.asset.ui.screen.HomeScreen
import com.bear.asset.ui.screen.ReportScreen
import com.bear.asset.ui.screen.SettingsScreen

object NavRoutes {
    const val LOGIN = "login"
}

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val startDestination = if (tokenManager.isLoggedIn()) {
        BottomNavItem.Home.route
    } else {
        NavRoutes.LOGIN
    }

    val showBottomBar = currentDestination?.route != NavRoutes.LOGIN

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.LOGIN) {
                val biometricEnabled = tokenManager.isBiometricEnabled() && tokenManager.getToken() != null

                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    showBiometric = biometricEnabled,
                    onBiometricSuccess = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Assets.route) { AssetScreen() }
            composable(BottomNavItem.Report.route) { ReportScreen() }
            composable(BottomNavItem.Ai.route) { AiScreen() }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
