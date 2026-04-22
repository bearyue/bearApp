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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
private val BorderLight = Color(0xFFEEF0F4)

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
        CategoryGridRow(AssetCategory.LIQUID, AssetCategory.INVESTMENT, onSelect)
        Spacer(modifier = Modifier.height(12.dp))
        CategoryGridRow(AssetCategory.RESTRICTED, AssetCategory.PHYSICAL, onSelect)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryCard(AssetCategory.LIABILITY, { onSelect(AssetCategory.LIABILITY) }, Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CategoryGridRow(left: AssetCategory, right: AssetCategory, onSelect: (AssetCategory) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CategoryCard(left, { onSelect(left) }, Modifier.weight(1f))
        CategoryCard(right, { onSelect(right) }, Modifier.weight(1f))
    }
}

@Composable
private fun CategoryCard(category: AssetCategory, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val icon = getCategoryIcon(category)
    val tint = getCategoryColor(category)
    Card(
        modifier = modifier.height(132.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            IconBubble(icon = icon, tint = tint, size = 44.dp, iconSize = 24.dp, rounded = true)
            Column {
                Text(category.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(getCategoryDescription(category), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
private fun SubTypeSelectionStep(category: AssetCategory, onSelect: (AssetSubType) -> Unit) {
    val subTypes = AssetSubType.entries.filter { it.category == category }
    val tint = getCategoryColor(category)

    Surface(color = PageBackground, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("${category.displayName} · ${getCategoryDescription(category)}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 16.dp))
            subTypes.forEach { subType ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { onSelect(subType) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconBubble(icon = getCategoryIcon(category), tint = tint, size = 38.dp, iconSize = 20.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(subType.displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun AssetFormStep(state: AddAssetState, viewModel: AddAssetViewModel) {
    val subType = state.selectedSubType ?: return
    val category = state.selectedCategory ?: subType.category
    val tint = getCategoryColor(category)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FormHeaderCard(category = category, subType = subType)

        FormSection(title = "基础信息") {
            FormTextField(value = state.name, onValueChange = viewModel::updateName, label = "名称 *")
            Spacer(modifier = Modifier.height(12.dp))
            CurrencySelector(selected = state.currency, onSelect = viewModel::updateCurrency)
        }

        FormSection(title = getDynamicSectionTitle(subType)) {
            DynamicFields(subType = subType, state = state, viewModel = viewModel)
        }

        FormSection(title = "备注信息") {
            FormTextField(
                value = state.note,
                onValueChange = viewModel::updateNote,
                label = "备注",
                minLines = 2,
                maxLines = 4
            )
        }

        if (state.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = state.errorMessage!!,
                    color = DangerRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Button(
            onClick = viewModel::save,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = !state.isSaving,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = tint)
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("保存资产", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FormHeaderCard(category: AssetCategory, subType: AssetSubType) {
    val tint = getCategoryColor(category)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            IconBubble(icon = getCategoryIcon(category), tint = tint, size = 48.dp, iconSize = 25.dp, rounded = true)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(subType.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${category.displayName} · ${getCategoryDescription(category)}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable Column.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(start = 2.dp, bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp), content = content)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = maxLines == 1,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandBlue,
            unfocusedBorderColor = BorderLight,
            focusedContainerColor = Color(0xFFFAFBFC),
            unfocusedContainerColor = Color(0xFFFAFBFC)
        )
    )
}

@Composable
private fun DynamicFields(subType: AssetSubType, state: AddAssetState, viewModel: AddAssetViewModel) {
    val fields = when (subType) {
        AssetSubType.STOCK_A, AssetSubType.STOCK_HK, AssetSubType.STOCK_US -> listOf(
            FieldSpec(state.code, viewModel::updateCode, "股票代码"),
            FieldSpec(state.quantity, viewModel::updateQuantity, "持仓数量", KeyboardType.Decimal),
            FieldSpec(state.cost, viewModel::updateCost, "成本价", KeyboardType.Decimal),
            FieldSpec(state.amount, viewModel::updateAmount, "现价", KeyboardType.Decimal)
        )
        AssetSubType.PUBLIC_FUND -> listOf(
            FieldSpec(state.code, viewModel::updateCode, "基金代码"),
            FieldSpec(state.quantity, viewModel::updateQuantity, "持有份额", KeyboardType.Decimal),
            FieldSpec(state.amount, viewModel::updateAmount, "单位净值", KeyboardType.Decimal)
        )
        AssetSubType.BANK_FINANCIAL, AssetSubType.TIME_DEPOSIT, AssetSubType.LARGE_DEPOSIT -> listOf(
            FieldSpec(state.amount, viewModel::updateAmount, "本金", KeyboardType.Decimal),
            FieldSpec(state.rate, viewModel::updateRate, "年利率 (%)", KeyboardType.Decimal),
            FieldSpec(state.maturityDate, viewModel::updateMaturityDate, "到期日 (yyyy-MM-dd)")
        )
        AssetSubType.ADVISOR, AssetSubType.CUSTOM_INVESTMENT -> listOf(
            FieldSpec(state.amount, viewModel::updateAmount, "当前市值", KeyboardType.Decimal),
            FieldSpec(state.cost, viewModel::updateCost, "投入成本", KeyboardType.Decimal)
        )
        AssetSubType.PROVIDENT_FUND -> listOf(
            FieldSpec(state.amount, viewModel::updateAmount, "账户余额", KeyboardType.Decimal),
            FieldSpec(state.monthlyBase, viewModel::updateMonthlyBase, "月缴存基数", KeyboardType.Decimal),
            FieldSpec(state.personalRate, viewModel::updatePersonalRate, "个人缴存比例 (%)", KeyboardType.Decimal),
            FieldSpec(state.companyRate, viewModel::updateCompanyRate, "单位缴存比例 (%)", KeyboardType.Decimal)
        )
        AssetSubType.INSURANCE -> listOf(
            FieldSpec(state.premiumPaid, viewModel::updatePremiumPaid, "已缴保费", KeyboardType.Decimal),
            FieldSpec(state.cashValue, viewModel::updateCashValue, "现金价值", KeyboardType.Decimal)
        )
        AssetSubType.REAL_ESTATE, AssetSubType.VEHICLE -> listOf(
            FieldSpec(state.cost, viewModel::updateCost, "购入价格", KeyboardType.Decimal),
            FieldSpec(state.amount, viewModel::updateAmount, "当前估值", KeyboardType.Decimal)
        )
        AssetSubType.MORTGAGE, AssetSubType.CAR_LOAN -> listOf(
            FieldSpec(state.totalLoan, viewModel::updateTotalLoan, "贷款总额", KeyboardType.Decimal),
            FieldSpec(state.remainingPrincipal, viewModel::updateRemainingPrincipal, "剩余本金", KeyboardType.Decimal),
            FieldSpec(state.monthlyPayment, viewModel::updateMonthlyPayment, "月供", KeyboardType.Decimal),
            FieldSpec(state.loanRate, viewModel::updateLoanRate, "贷款利率 (%)", KeyboardType.Decimal),
            FieldSpec(state.amount, viewModel::updateAmount, "当前余额（用于资产计算）", KeyboardType.Decimal)
        )
        AssetSubType.CREDIT_CARD -> listOf(
            FieldSpec(state.amount, viewModel::updateAmount, "待还金额", KeyboardType.Decimal)
        )
        else -> listOf(
            FieldSpec(state.amount, viewModel::updateAmount, "金额", KeyboardType.Decimal)
        )
    }

    fields.forEachIndexed { index, field ->
        FormTextField(
            value = field.value,
            onValueChange = field.onValueChange,
            label = field.label,
            keyboardType = field.keyboardType
        )
        if (index != fields.lastIndex) Spacer(modifier = Modifier.height(12.dp))
    }
}

private data class FieldSpec(
    val value: String,
    val onValueChange: (String) -> Unit,
    val label: String,
    val keyboardType: KeyboardType = KeyboardType.Text
)

private fun getDynamicSectionTitle(subType: AssetSubType): String = when (subType) {
    AssetSubType.STOCK_A, AssetSubType.STOCK_HK, AssetSubType.STOCK_US -> "持仓信息"
    AssetSubType.PUBLIC_FUND -> "基金信息"
    AssetSubType.BANK_FINANCIAL, AssetSubType.TIME_DEPOSIT, AssetSubType.LARGE_DEPOSIT -> "理财信息"
    AssetSubType.ADVISOR, AssetSubType.CUSTOM_INVESTMENT -> "投资信息"
    AssetSubType.PROVIDENT_FUND -> "公积金信息"
    AssetSubType.INSURANCE -> "保险信息"
    AssetSubType.REAL_ESTATE, AssetSubType.VEHICLE -> "估值信息"
    AssetSubType.MORTGAGE, AssetSubType.CAR_LOAN, AssetSubType.CREDIT_CARD -> "负债信息"
    else -> "金额信息"
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    tint: Color,
    size: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    rounded: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(if (rounded) RoundedCornerShape(14.dp) else CircleShape)
            .background(tint.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize), tint = tint)
    }
}

@Composable
private fun CurrencySelector(selected: Currency, onSelect: (Currency) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("${selected.symbol} ${selected.displayName}", color = TextPrimary, fontWeight = FontWeight.Medium)
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
