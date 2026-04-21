package com.bear.asset.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bear.asset.data.local.entity.AssetEntity
import com.bear.asset.data.repository.AssetRepository
import com.bear.asset.domain.model.AssetCategory
import com.bear.asset.domain.model.AssetSubType
import com.bear.asset.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetDetailState(
    val asset: AssetEntity? = null,
    val isEditing: Boolean = false,
    val editName: String = "",
    val editAmount: String = "",
    val editCost: String = "",
    val editQuantity: String = "",
    val editCode: String = "",
    val editNote: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isDeleted: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AssetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AssetRepository
) : ViewModel() {

    private val assetId: Long = savedStateHandle.get<Long>("assetId") ?: -1L

    private val _state = MutableStateFlow(AssetDetailState())
    val state: StateFlow<AssetDetailState> = _state.asStateFlow()

    init {
        loadAsset()
    }

    private fun loadAsset() {
        viewModelScope.launch {
            val asset = repository.getAssetById(assetId)
            if (asset != null) {
                _state.value = _state.value.copy(
                    asset = asset,
                    isLoading = false,
                    editName = asset.name,
                    editAmount = asset.amount.toString(),
                    editCost = asset.cost?.toString() ?: "",
                    editQuantity = asset.quantity?.toString() ?: "",
                    editCode = asset.code ?: "",
                    editNote = asset.note ?: ""
                )
            } else {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "资产不存在")
            }
        }
    }

    fun toggleEdit() {
        val current = _state.value
        if (current.isEditing) {
            // Cancel edit, restore original values
            val asset = current.asset ?: return
            _state.value = current.copy(
                isEditing = false,
                editName = asset.name,
                editAmount = asset.amount.toString(),
                editCost = asset.cost?.toString() ?: "",
                editQuantity = asset.quantity?.toString() ?: "",
                editCode = asset.code ?: "",
                editNote = asset.note ?: ""
            )
        } else {
            _state.value = current.copy(isEditing = true)
        }
    }

    fun updateName(v: String) { _state.value = _state.value.copy(editName = v) }
    fun updateAmount(v: String) { _state.value = _state.value.copy(editAmount = v) }
    fun updateCost(v: String) { _state.value = _state.value.copy(editCost = v) }
    fun updateQuantity(v: String) { _state.value = _state.value.copy(editQuantity = v) }
    fun updateCode(v: String) { _state.value = _state.value.copy(editCode = v) }
    fun updateNote(v: String) { _state.value = _state.value.copy(editNote = v) }

    fun showDeleteDialog() { _state.value = _state.value.copy(showDeleteDialog = true) }
    fun dismissDeleteDialog() { _state.value = _state.value.copy(showDeleteDialog = false) }

    fun saveEdit() {
        val current = _state.value
        val asset = current.asset ?: return

        viewModelScope.launch {
            _state.value = current.copy(isSaving = true)
            try {
                val newAmount = current.editAmount.toDoubleOrNull() ?: asset.amount
                val updated = asset.copy(
                    name = current.editName.ifBlank { asset.name },
                    amount = newAmount,
                    amountCny = newAmount,
                    cost = current.editCost.toDoubleOrNull(),
                    quantity = current.editQuantity.toDoubleOrNull(),
                    code = current.editCode.ifBlank { null },
                    note = current.editNote.ifBlank { null },
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateAsset(updated)
                _state.value = _state.value.copy(
                    asset = updated,
                    isEditing = false,
                    isSaving = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "保存失败"
                )
            }
        }
    }

    fun deleteAsset() {
        viewModelScope.launch {
            repository.deleteAssetById(assetId)
            _state.value = _state.value.copy(isDeleted = true, showDeleteDialog = false)
        }
    }

    fun getCategoryDisplayName(): String {
        val asset = _state.value.asset ?: return ""
        return try { AssetCategory.valueOf(asset.category).displayName } catch (_: Exception) { asset.category }
    }

    fun getSubTypeDisplayName(): String {
        val asset = _state.value.asset ?: return ""
        return try { AssetSubType.valueOf(asset.subType).displayName } catch (_: Exception) { asset.subType }
    }

    fun getCurrencySymbol(): String {
        val asset = _state.value.asset ?: return "¥"
        return try { Currency.valueOf(asset.currency).symbol } catch (_: Exception) { "¥" }
    }

    fun getProfitLoss(): Double? {
        val asset = _state.value.asset ?: return null
        val cost = asset.cost ?: return null
        val qty = asset.quantity ?: return null
        if (cost == 0.0 || qty == 0.0) return null
        return asset.amount - (cost * qty)
    }
}
