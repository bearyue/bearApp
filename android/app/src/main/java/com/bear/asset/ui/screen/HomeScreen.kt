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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bear.asset.data.repository.CategorySummary
import com.bear.asset.domain.model.AssetCategory
import com.bear.asset.ui.util.NumberFormatter

private val PageBackground = Color(0xFFF7F8FA)
private val BrandBlue = Color(0xFF3478F6)
private val BrandBlueDeep = Color(0xFF2E6BE6)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF55C777)
private val DangerRed = Color(0xFFFF5B6D)
private val WarningOrange = Color(0xFFFFA64D)
private val Purple = Color(0xFF8557E8)

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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        NetWorthHeader(
                            netWorth = uiState.netWorth,
                            totalAsset = uiState.totalAsset,
                            totalLiability = uiState.totalLiability,
                            onNavigateToAi = onNavigateToAi
                        )
                    }
                    item {
                        Text(
                            "资产分布",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    item {
                        AssetDistributionGrid(
                            summaries = uiState.categorySummaries,
                            onNavigateToCategory = onNavigateToCategory
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NetWorthHeader(
    netWorth: Double,
    totalAsset: Double,
    totalLiability: Double,
    onNavigateToAi: () -> Unit
) {
    val animatedNetWorth by animateFloatAsState(
        targetValue = netWorth.toFloat(),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "netWorth"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Brush.linearGradient(colors = listOf(BrandBlue, BrandBlueDeep)))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.White.copy(alpha = 0.06f))
                )
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("净资产  CNY", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.92f), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White.copy(alpha = 0.72f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        AiPill(onClick = onNavigateToAi)
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        NumberFormatter.formatCurrency(animatedNetWorth.toDouble()),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFF))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryMetric("总资产", NumberFormatter.formatAbbreviated(totalAsset), SuccessGreen, Modifier.weight(1f))
                SummaryMetric("总负债", NumberFormatter.formatAbbreviated(totalLiability), DangerRed, Modifier.weight(1f))
                SummaryMetric("净资产", NumberFormatter.formatAbbreviated(netWorth), BrandBlueDeep, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AiPill(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interactionSource, 0.96f)
    Row(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(99.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.White.copy(alpha = 0.88f), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("资产分析", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f), maxLines = 1)
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AssetDistributionGrid(
    summaries: List<CategorySummary>,
    onNavigateToCategory: (AssetCategory) -> Unit
) {
    val ordered = AssetCategory.entries.map { category ->
        summaries.firstOrNull { it.category == category } ?: emptySummary(category)
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ordered.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { summary ->
                    DistributionTile(
                        summary = summary,
                        onClick = { onNavigateToCategory(summary.category) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun emptySummary(category: AssetCategory): CategorySummary = CategorySummary(category = category, totalAmount = 0.0, assetCount = 0)

@Composable
private fun DistributionTile(summary: CategorySummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val icon = getCategoryIcon(summary.category)
    val tint = getCategoryColor(summary.category)
    val isLiability = summary.category == AssetCategory.LIABILITY
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interactionSource, pressedScale = 0.975f)

    Card(
        modifier = modifier
            .height(104.dp)
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(tint),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    summary.category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    "${summary.assetCount}项${if (isLiability) "负债" else "资产"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                NumberFormatter.formatAbbreviated(summary.totalAmount),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isLiability) DangerRed else tint,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
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
    AssetCategory.INVESTMENT -> Purple
    AssetCategory.RESTRICTED -> SuccessGreen
    AssetCategory.PHYSICAL -> WarningOrange
    AssetCategory.LIABILITY -> DangerRed
}

@Composable
private fun EmptyState(onAddAsset: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interactionSource, pressedScale = 0.982f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onAddAsset),
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
private fun rememberPressScale(interactionSource: MutableInteractionSource, pressedScale: Float): androidx.compose.runtime.State<Float> {
    val pressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "pressScale"
    )
}
