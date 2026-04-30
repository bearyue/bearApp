package com.bear.asset.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

private val BrandBlue = Color(0xFF3478F6)
private val TextSecondary = Color(0xFF667085)
private val FloatingNavBackground = Color.White.copy(alpha = 0.94f)

object NavRoutes {
    const val LOGIN = "login"
    const val ADD_ASSET = "addAsset"
    const val ASSET_DETAIL = "assetDetail/{assetId}"
    const val SETTINGS = "settings"

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

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
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
                    onNavigateToAddAsset = { navController.navigate(NavRoutes.ADD_ASSET) },
                    onNavigateToAi = {
                        navController.navigate(BottomNavItem.Ai.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) },
                    onNavigateToCategory = { }
                )
            }

            composable(BottomNavItem.Assets.route) {
                AssetScreen(
                    onAddAsset = { navController.navigate(NavRoutes.ADD_ASSET) },
                    onAssetClick = { assetId -> navController.navigate(NavRoutes.assetDetail(assetId)) }
                )
            }

            composable(BottomNavItem.Report.route) { ReportScreen() }
            composable(BottomNavItem.Ai.route) { AiScreen() }

            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    isLoggedIn = tokenManager.isLoggedIn(),
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(NavRoutes.LOGIN) },
                    onLogout = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
                AssetDetailScreen(onNavigateBack = { navController.popBackStack() })
            }
        }

        if (showBottomBar) {
            FloatingBottomBar(
                currentRoute = currentDestination?.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun FloatingBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, top = 0.5.dp, end = 14.dp, bottom = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = FloatingNavBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem.items.forEach { item ->
                    BottomNavButton(item, currentRoute, onNavigate, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BottomNavButton(
    item: BottomNavItem,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = currentRoute == item.route
    val color = if (selected) BrandBlue else TextSecondary
    Column(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onNavigate(item.route) }
            .padding(start = 2.dp, top = 5.dp, end = 2.dp, bottom = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(26.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(if (selected) BrandBlue.copy(alpha = 0.10f) else Color.Transparent)
                .padding(horizontal = if (selected) 11.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(20.dp),
                tint = color
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            item.label,
            color = color,
            fontSize = 11.sp,
            lineHeight = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
