package com.bear.asset.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
private val BrandPurple = Color(0xFF6C4DFF)
private val TextSecondary = Color(0xFF667085)
private val FloatingNavBackground = Color.White.copy(alpha = 0.94f)

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
        containerColor = Color.Transparent,
        bottomBar = {
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
                    onAddAsset = { navController.navigate(NavRoutes.ADD_ASSET) }
                )
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
                    onNavigateToAddAsset = { navController.navigate(NavRoutes.ADD_ASSET) },
                    onNavigateToAi = {
                        navController.navigate(BottomNavItem.Ai.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
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

            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    isLoggedIn = tokenManager.isLoggedIn(),
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
    }
}

@Composable
private fun FloatingBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAddAsset: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = FloatingNavBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavButton(BottomNavItem.Home, currentRoute, onNavigate, Modifier.weight(1f))
                BottomNavButton(BottomNavItem.Assets, currentRoute, onNavigate, Modifier.weight(1f))
                CenterAddButton(onAddAsset, Modifier.weight(1f))
                BottomNavButton(BottomNavItem.Report, currentRoute, onNavigate, Modifier.weight(1f))
                BottomNavButton(BottomNavItem.Ai, currentRoute, onNavigate, Modifier.weight(1f))
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
            .height(62.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onNavigate(item.route) }
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(if (selected) BrandBlue.copy(alpha = 0.10f) else Color.Transparent)
                .padding(horizontal = if (selected) 13.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(22.dp),
                tint = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            item.label,
            color = color,
            fontSize = 12.sp,
            lineHeight = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun CenterAddButton(onAddAsset: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(62.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onAddAsset)
            .padding(horizontal = 2.dp, vertical = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(BrandBlue, BrandPurple))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "添加资产",
                modifier = Modifier.size(27.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            "添加",
            color = BrandBlue,
            fontSize = 12.sp,
            lineHeight = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            fontWeight = FontWeight.SemiBold
        )
    }
}
