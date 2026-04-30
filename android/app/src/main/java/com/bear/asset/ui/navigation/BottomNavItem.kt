package com.bear.asset.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "home",
        label = "首页",
        icon = Icons.Default.Home
    )

    data object Assets : BottomNavItem(
        route = "assets",
        label = "资产",
        icon = Icons.Default.List
    )

    data object Report : BottomNavItem(
        route = "report",
        label = "报表",
        icon = Icons.Default.BarChart
    )

    data object Ai : BottomNavItem(
        route = "ai",
        label = "AI",
        icon = Icons.Default.Chat
    )

    companion object {
        val items: List<BottomNavItem>
            get() = listOf(Home, Assets, Report, Ai)
    }
}
