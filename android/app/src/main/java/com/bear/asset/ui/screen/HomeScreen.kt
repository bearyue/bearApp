package com.bear.asset.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bear.asset.data.repository.CategorySummary
import com.bear.asset.domain.model.AssetCategory
import com.bear.asset.ui.util.NumberFormatter

private val PageBackground = Color(0xFFF7F8FA)
private val BrandBlue = Color(0xFF2563EB)
private val BrandPurple = Color(0xFF7C3AED)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val BorderLight = Color(0xFFEEF0F4)
private val SuccessGreen = Color(0xFF16A34A)
private val DangerRed = Color(0xFFEF4444)
private val WarningOrange = Color(0xFFF59E0B)

@Composable
fun HomeScreen(
    onNavigateToAddAsset: () -> Unit = {},
    onNavigateToAi: () -> Unit = {},
    onNavigateToCategory: (AssetCategory) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) contentVisible = true
    }

    Surface(modifier = Modifier.fillMaxSize(), color = PageBackground) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandBlue)
            }
        } else {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(260)) + slideInVertically(
                    animationSpec = tween(360, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 18 }
                )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { HeaderTitle() }
                    item { NetWorthHeader(uiState.netWorth, uiState.totalAsset, uiState.totalLiability, uiState.dailyChange, uiState.dailyChangePercent) }
                    item { QuickActions(onNavigateToAddAsset, onAssets = { }, onNavigateToAi) }

                    if (uiState.categorySummaries.isNotEmpty()) {
                        item { SectionHeader(title = "资产分布", action = "查看全部") }
                        items(uiState.categorySummaries) { summary ->
                            CategoryCard(summary = summary, totalAsset = uiState.totalAsset, onClick = { onNavigateToCategory(summary.category) })
                        }
                    } else {
                        item { EmptyState(onAddAsset = onNavigateToAddAsset) }
                    }

                    item {
                        SectionHeader(title = "最近记录", action = null)
                        RecentEmptyCard()
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun HeaderTitle() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 2.dp)) {
        Text("资产总览", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("清晰记录每一份资产变化", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun NetWorthHeader(netWorth: Double, totalAsset: Double, totalLiability: Double, dailyChange: Double, dailyChangePercent: Double) {
    val animatedNetWorth by animateFloatAsState(
        targetValue = netWorth.toFloat(),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "netWorth"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(colors = listOf(BrandBlue, BrandPurple))).padding(22.dp)) {
            Text("净资产", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.78f))
            Spacer(modifier = Modifier.height(6.dp))
            Text(NumberFormatter.formatCurrency(animatedNetWorth.toDouble()), style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp), fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            val changeText = if (dailyChange == 0.0) "今日暂无变化" else {
                val arrow = if (dailyChange >= 0) "▲" else "▼"
                "$arrow 今日 ${NumberFormatter.formatWithSign(dailyChange)} (${NumberFormatter.formatPercent(dailyChangePercent)})"
            }
            Text(changeText, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HeaderMetric("总资产", NumberFormatter.formatAbbreviated(totalAsset), Modifier.weight(1f))
                HeaderMetric("总负债", NumberFormatter.formatAbbreviated(totalLiability), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun HeaderMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.14f)).padding(horizontal = 14.dp, vertical = 12.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.72f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun QuickActions(onAddAsset: () -> Unit, onAssets: () -> Unit, onAi: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickActionButton(Icons.Default.Add, "添加资产", BrandBlue, onAddAsset, Modifier.weight(1f))
        QuickActionButton(Icons.Default.AccountBalanceWallet, "钱包账户", SuccessGreen, onAssets, Modifier.weight(1f))
        QuickActionButton(Icons.Default.Chat, "AI 分析", BrandPurple, onAi, Modifier.weight(1f))
    }
}

@Composable
private fun QuickActionButton(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interactionSource, pressedScale = 0.965f)

    Card(
        modifier = modifier.scale(scale).clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(34.dp).clip(CircleShape).background(tint.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(19.dp), tint = tint)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
        if (action != null) Text(action, style = MaterialTheme.typography.bodySmall, color = BrandBlue)
    }
}

@Composable
private fun CategoryCard(summary: CategorySummary, totalAsset: Double, onClick: () -> Unit) {
    val icon = getCategoryIcon(summary.category)
    val tint = getCategoryColor(summary.category)
    val isLiability = summary.category == AssetCategory.LIABILITY
    val percent = if (totalAsset > 0) (summary.totalAmount / totalAsset).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (isLiability) 1f else percent,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "assetDistributionProgress"
    )
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interactionSource, pressedScale = 0.982f)

    Card(
        modifier = Modifier.fillMaxWidth().scale(scale).clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(tint.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = tint)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(summary.category.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${summary.assetCount}项资产", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(NumberFormatter.formatAbbreviated(summary.totalAmount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isLiability) DangerRed else TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${(percent * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(progress = { animatedProgress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)), color = tint, trackColor = BorderLight)
        }
    }
}

private fun getCategoryIcon(category: AssetCategory): ImageVector = when (category) {
    AssetCategory.LIQUID -> Icons.Default.AccountBalanceWallet
    AssetCategory.INVESTMENT -> Icons.Default.TrendingUp
    AssetCategory.RESTRICTED -> Icons.Default.Lock
    AssetCategory.PHYSICAL -> Icons.Default.Home
    AssetCategory.LIABILITY -> Icons.Default.CreditCard
}

private fun getCategoryColor(category: AssetCategory): Color = when (category) {
    AssetCategory.LIQUID -> BrandBlue
    AssetCategory.INVESTMENT -> SuccessGreen
    AssetCategory.RESTRICTED -> WarningOrange
    AssetCategory.PHYSICAL -> BrandPurple
    AssetCategory.LIABILITY -> DangerRed
}

@Composable
private fun RecentEmptyCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Text("暂无记录，添加资产后会在这里展示最近变化", modifier = Modifier.fillMaxWidth().padding(18.dp), style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun EmptyState(onAddAsset: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interactionSource, pressedScale = 0.982f)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).scale(scale).clickable(interactionSource = interactionSource, indication = null, onClick = onAddAsset),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(BrandBlue.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(34.dp), tint = BrandBlue)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("开始记录你的资产", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("添加你的第一笔资产，掌握财务全貌", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun rememberPressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float
): androidx.compose.runtime.State<Float> {
    val pressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "pressScale"
    )
}
