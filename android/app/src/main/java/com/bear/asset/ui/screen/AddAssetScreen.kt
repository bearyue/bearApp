package com.bear.asset.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bear.asset.domain.model.AssetCategory
import com.bear.asset.domain.model.AssetSubType
import com.bear.asset.domain.model.Currency

private val PageBackground = Color(0xFFF7F8FA)
private val BrandBlue = Color(0xFF2563EB)
private val BrandPurple = Color(0xFF7C3AED)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF16A34A)
private val DangerRed = Color(0xFFEF4444)
private val WarningOrange = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    viewModel: AddAssetViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) onSaveSuccess()
    }

    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBackground),
                title = {
                    Text(
                        when (state.step) {
                            1 -> "选择资产类别"
                            2 -> "选择资产类型"
                            else -> "填写资产信息"
                        },
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.step > 1) viewModel.goBack()
                        else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = TextPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state.step) {
                1 -> CategorySelectionStep(onSelect = viewModel::selectCategory)
                2 -> SubTypeSelectionStep(
                    category = state.selectedCategory!!,
                    onSelect = viewModel::selectSubType
                )
                3 -> AssetFormStep(
                    state = state,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun CategorySelectionStep(onSelect: (AssetCategory) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "请选择要添加的资产类型",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 18.dp)
        )
        CategoryGridRow(
            left = AssetCategory.LIQUID,
            right = AssetCategory.INVESTMENT,
            onSelect = onSelect
        )
        Spacer(modifier = Modifier.height(12.dp))
        CategoryGridRow(
            left = AssetCategory.RESTRICTED,
            right = AssetCategory.PHYSICAL,
            onSelect = onSelect
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryCard(
                category = AssetCategory.LIABILITY,
                onClick = { onSelect(AssetCategory.LIABILITY) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CategoryGridRow(
    left: AssetCategory,
    right: AssetCategory,
    onSelect: (AssetCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CategoryCard(
            category = left,
            onClick = { onSelect(left) },
            modifier = Modifier.weight(1f)
        )
        CategoryCard(
            category = right,
            onClick = { onSelect(right) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryCard(
    category: AssetCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = getCategoryIcon(category)
    val tint = getCategoryColor(category)
    Card(
        modifier = modifier
            .height(132.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tint.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = tint)
            }
            Column {
                Text(
                    category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    getCategoryDescription(category),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
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

private fun getCategoryDescription(category: AssetCategory): String = when (category) {
    AssetCategory.LIQUID -> "现金 / 银行卡 / 余额"
    AssetCategory.INVESTMENT -> "股票 / 基金 / 理财"
    AssetCategory.RESTRICTED -> "公积金 / 保险 / 押金"
    AssetCategory.PHYSICAL -> "房产 / 车辆 / 实物"
    AssetCategory.LIABILITY -> "信用卡 / 房贷 / 车贷"
}

@Composable
private fun SubTypeSelectionStep(
    category: AssetCategory,
    onSelect: (AssetSubType) -> Unit
) {
    val subTypes = AssetSubType.entries.filter { it.category == category }
    val tint = getCategoryColor(category)

    Surface(color = PageBackground, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "${category.displayName} · ${getCategoryDescription(category)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            subTypes.forEach { subType ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onSelect(subType) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(tint.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                getCategoryIcon(category),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = tint
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            subType.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssetFormStep(
    state: AddAssetState,
    viewModel: AddAssetViewModel
) {
    val subType = state.selectedSubType ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::updateName,
            label = { Text("名称 *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        CurrencySelector(
            selected = state.currency,
            onSelect = viewModel::updateCurrency
        )

        when (subType) {
            AssetSubType.STOCK_A, AssetSubType.STOCK_HK, AssetSubType.STOCK_US -> {
                OutlinedTextField(value = state.code, onValueChange = viewModel::updateCode, label = { Text("股票代码") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = state.quantity, onValueChange = viewModel::updateQuantity, label = { Text("持仓数量") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.cost, onValueChange = viewModel::updateCost, label = { Text("成本价") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("现价") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            AssetSubType.PUBLIC_FUND -> {
                OutlinedTextField(value = state.code, onValueChange = viewModel::updateCode, label = { Text("基金代码") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = state.quantity, onValueChange = viewModel::updateQuantity, label = { Text("持有份额") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("单位净值") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            AssetSubType.BANK_FINANCIAL, AssetSubType.TIME_DEPOSIT, AssetSubType.LARGE_DEPOSIT -> {
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("本金") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.rate, onValueChange = viewModel::updateRate, label = { Text("年利率 (%)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.maturityDate, onValueChange = viewModel::updateMaturityDate, label = { Text("到期日 (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            AssetSubType.ADVISOR, AssetSubType.CUSTOM_INVESTMENT -> {
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("当前市值") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.cost, onValueChange = viewModel::updateCost, label = { Text("投入成本") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            AssetSubType.PROVIDENT_FUND -> {
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("账户余额") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.monthlyBase, onValueChange = viewModel::updateMonthlyBase, label = { Text("月缴存基数") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.personalRate, onValueChange = viewModel::updatePersonalRate, label = { Text("个人缴存比例 (%)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.companyRate, onValueChange = viewModel::updateCompanyRate, label = { Text("单位缴存比例 (%)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            AssetSubType.INSURANCE -> {
                OutlinedTextField(value = state.premiumPaid, onValueChange = viewModel::updatePremiumPaid, label = { Text("已缴保费") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.cashValue, onValueChange = viewModel::updateCashValue, label = { Text("现金价值") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            AssetSubType.REAL_ESTATE, AssetSubType.VEHICLE -> {
                OutlinedTextField(value = state.cost, onValueChange = viewModel::updateCost, label = { Text("购入价格") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("当前估值") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            AssetSubType.MORTGAGE, AssetSubType.CAR_LOAN -> {
                OutlinedTextField(value = state.totalLoan, onValueChange = viewModel::updateTotalLoan, label = { Text("贷款总额") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.remainingPrincipal, onValueChange = viewModel::updateRemainingPrincipal, label = { Text("剩余本金") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.monthlyPayment, onValueChange = viewModel::updateMonthlyPayment, label = { Text("月供") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.loanRate, onValueChange = viewModel::updateLoanRate, label = { Text("贷款利率 (%)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("当前余额（用于资产计算）") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            AssetSubType.CREDIT_CARD -> {
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("待还金额") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
            else -> {
                OutlinedTextField(value = state.amount, onValueChange = viewModel::updateAmount, label = { Text("金额") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
        }

        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::updateNote,
            label = { Text("备注") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = viewModel::save,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !state.isSaving
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("保存", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CurrencySelector(
    selected: Currency,
    onSelect: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("${selected.symbol} ${selected.displayName}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Currency.entries.forEach { currency ->
                DropdownMenuItem(
                    text = { Text("${currency.symbol} ${currency.displayName}") },
                    onClick = {
                        onSelect(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}
