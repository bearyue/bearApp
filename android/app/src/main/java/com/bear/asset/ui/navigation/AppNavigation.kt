package com.bear.asset.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bear.asset.data.local.TokenManager
import com.bear.asset.ui.auth.LoginScreen
import com.bear.asset.ui.screen.AddAssetScreen
import com.bear.asset.ui.screen.AiScreen
import com.bear.asset.ui.screen.AssetDetailScreen
import com.bear.asset.ui.screen.AssetScreen
import com.bear.asset.ui.screen.HomeScreen
import com.bear.asset.ui.screen.ReportScreen
import com.bear.asset.ui.screen.SettingsScreen

object NavRoutes {
    const val LOGIN = "login"
    const val ADD_ASSET = "addAsset"
    const val ASSET_DETAIL = "assetDetail/{assetId}"

    fun assetDetail(assetId: Long) = "assetDetail/$assetId"
}

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val startDestination = BottomNavItem.Home.route

    val bottomBarRoutes = BottomNavItem.items.map { it.route }.toSet()
    val showBottomBar = currentDestination?.route in bottomBarRoutes

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

            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToAddAsset = {
                        navController.navigate(NavRoutes.ADD_ASSET)
                    },
                    onNavigateToAi = {
                        navController.navigate(BottomNavItem.Ai.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToCategory = { /* future: filter asset list */ }
                )
            }

            composable(BottomNavItem.Assets.route) {
                AssetScreen(
                    onAddAsset = {
                        navController.navigate(NavRoutes.ADD_ASSET)
                    },
                    onAssetClick = { assetId ->
                        navController.navigate(NavRoutes.assetDetail(assetId))
                    }
                )
            }

            composable(BottomNavItem.Report.route) { ReportScreen() }
            composable(BottomNavItem.Ai.route) { AiScreen() }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    isLoggedIn = tokenManager.isLoggedIn(),
                    onNavigateToLogin = {
                        navController.navigate(NavRoutes.LOGIN)
                    },
                    onLogout = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(NavRoutes.ADD_ASSET) {
                AddAssetScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.ASSET_DETAIL,
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) {
                AssetDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
