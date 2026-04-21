package com.bear.asset.ui.screen

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bear.asset.ui.util.NumberFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AssetDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onNavigateBack()
    }

    // Delete confirmation dialog
    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text("确认删除") },
            text = { Text("确定要删除「${state.asset?.name}」吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = viewModel::deleteAsset,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteDialog) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.asset?.name ?: "资产详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (state.isEditing) {
                        IconButton(onClick = viewModel::saveEdit) {
                            Icon(Icons.Default.Save, contentDescription = "保存")
                        }
                    }
                    IconButton(onClick = viewModel::toggleEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = if (state.isEditing) "取消编辑" else "编辑",
                            tint = if (state.isEditing) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = viewModel::showDeleteDialog) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.asset == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("资产不存在", color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                val asset = state.asset!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Value card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "当前价值",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${viewModel.getCurrencySymbol()}${NumberFormatter.formatAmount(asset.amount)}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            val profitLoss = viewModel.getProfitLoss()
                            if (profitLoss != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                val color = if (profitLoss >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                                val arrow = if (profitLoss >= 0) "▲" else "▼"
                                Text(
                                    text = "$arrow ${NumberFormatter.formatWithSign(profitLoss)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = color,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Info card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (state.isEditing) {
                                EditableInfoSection(state, viewModel)
                            } else {
                                ReadOnlyInfoSection(
                                    asset = asset,
                                    categoryName = viewModel.getCategoryDisplayName(),
                                    subTypeName = viewModel.getSubTypeDisplayName()
                                )
                            }
                        }
                    }

                    if (state.errorMessage != null) {
                        Text(
                            state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyInfoSection(
    asset: com.bear.asset.data.local.entity.AssetEntity,
    categoryName: String,
    subTypeName: String
) {
    InfoRow("类别", categoryName)
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    InfoRow("类型", subTypeName)
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    InfoRow("名称", asset.name)
    if (asset.code != null) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        InfoRow("代码", asset.code)
    }
    if (asset.quantity != null) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        InfoRow("数量", NumberFormatter.formatAmount(asset.quantity))
    }
    if (asset.cost != null) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        InfoRow("成本", NumberFormatter.formatAmount(asset.cost))
    }
    if (asset.note != null) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        InfoRow("备注", asset.note)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    InfoRow("币种", asset.currency)
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    InfoRow("人民币价值", "¥${NumberFormatter.formatAmount(asset.amountCny)}")
}

@Composable
private fun EditableInfoSection(
    state: AssetDetailState,
    viewModel: AssetDetailViewModel
) {
    OutlinedTextField(
        value = state.editName,
        onValueChange = viewModel::updateName,
        label = { Text("名称") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = state.editAmount,
        onValueChange = viewModel::updateAmount,
        label = { Text("金额/现价") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = state.editCode,
        onValueChange = viewModel::updateCode,
        label = { Text("代码") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = state.editQuantity,
        onValueChange = viewModel::updateQuantity,
        label = { Text("数量") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = state.editCost,
        onValueChange = viewModel::updateCost,
        label = { Text("成本") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = state.editNote,
        onValueChange = viewModel::updateNote,
        label = { Text("备注") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 4
    )

    if (state.isSaving) {
        Spacer(modifier = Modifier.height(8.dp))
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
