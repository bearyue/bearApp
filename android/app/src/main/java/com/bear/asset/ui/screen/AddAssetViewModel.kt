package com.bear.asset.ui.screen

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

data class AddAssetState(
    val step: Int = 1,
    val selectedCategory: AssetCategory? = null,
    val selectedSubType: AssetSubType? = null,
    val name: String = "",
    val currency: Currency = Currency.CNY,
    val amount: String = "",
    val cost: String = "",
    val quantity: String = "",
    val code: String = "",
    val note: String = "",
    // Fixed income fields
    val rate: String = "",
    val maturityDate: String = "",
    // Provident fund fields
    val monthlyBase: String = "",
    val personalRate: String = "",
    val companyRate: String = "",
    // Insurance fields
    val premiumPaid: String = "",
    val cashValue: String = "",
    // Loan fields
    val totalLoan: String = "",
    val remainingPrincipal: String = "",
    val monthlyPayment: String = "",
    val loanRate: String = "",
    // UI state
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddAssetViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddAssetState())
    val state: StateFlow<AddAssetState> = _state.asStateFlow()

    fun selectCategory(category: AssetCategory) {
        _state.value = _state.value.copy(selectedCategory = category, step = 2)
    }

    fun selectSubType(subType: AssetSubType) {
        _state.value = _state.value.copy(
            selectedSubType = subType,
            name = subType.displayName,
            step = 3
        )
    }

    fun goBack() {
        val current = _state.value
        when (current.step) {
            2 -> _state.value = current.copy(step = 1, selectedCategory = null)
            3 -> _state.value = current.copy(step = 2, selectedSubType = null)
        }
    }

    fun updateName(value: String) { _state.value = _state.value.copy(name = value) }
    fun updateCurrency(value: Currency) { _state.value = _state.value.copy(currency = value) }
    fun updateAmount(value: String) { _state.value = _state.value.copy(amount = value) }
    fun updateCost(value: String) { _state.value = _state.value.copy(cost = value) }
    fun updateQuantity(value: String) { _state.value = _state.value.copy(quantity = value) }
    fun updateCode(value: String) { _state.value = _state.value.copy(code = value) }
    fun updateNote(value: String) { _state.value = _state.value.copy(note = value) }
    fun updateRate(value: String) { _state.value = _state.value.copy(rate = value) }
    fun updateMaturityDate(value: String) { _state.value = _state.value.copy(maturityDate = value) }
    fun updateMonthlyBase(value: String) { _state.value = _state.value.copy(monthlyBase = value) }
    fun updatePersonalRate(value: String) { _state.value = _state.value.copy(personalRate = value) }
    fun updateCompanyRate(value: String) { _state.value = _state.value.copy(companyRate = value) }
    fun updatePremiumPaid(value: String) { _state.value = _state.value.copy(premiumPaid = value) }
    fun updateCashValue(value: String) { _state.value = _state.value.copy(cashValue = value) }
    fun updateTotalLoan(value: String) { _state.value = _state.value.copy(totalLoan = value) }
    fun updateRemainingPrincipal(value: String) { _state.value = _state.value.copy(remainingPrincipal = value) }
    fun updateMonthlyPayment(value: String) { _state.value = _state.value.copy(monthlyPayment = value) }
    fun updateLoanRate(value: String) { _state.value = _state.value.copy(loanRate = value) }

    fun save() {
        val s = _state.value
        val subType = s.selectedSubType ?: return
        val category = s.selectedCategory ?: return

        if (s.name.isBlank()) {
            _state.value = s.copy(errorMessage = "请输入名称")
            return
        }

        viewModelScope.launch {
            _state.value = s.copy(isSaving = true, errorMessage = null)
            try {
                val amount = computeAmount(subType, s)
                val amountCny = amount // simplified: same for CNY
                val cost = s.cost.toDoubleOrNull()
                val quantity = s.quantity.toDoubleOrNull()
                val extraJson = buildExtraJson(subType, s)

                val entity = AssetEntity(
                    category = category.name,
                    subType = subType.name,
                    name = s.name,
                    currency = s.currency.name,
                    amount = amount,
                    amountCny = amountCny,
                    cost = cost,
                    quantity = quantity,
                    code = s.code.ifBlank { null },
                    extraJson = extraJson,
                    note = s.note.ifBlank { null }
                )

                repository.insertAsset(entity)
                _state.value = _state.value.copy(isSaving = false, saveSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, errorMessage = e.message ?: "保存失败")
            }
        }
    }

    private fun computeAmount(subType: AssetSubType, s: AddAssetState): Double {
        return when (subType) {
            AssetSubType.STOCK_A, AssetSubType.STOCK_HK, AssetSubType.STOCK_US -> {
                val qty = s.quantity.toDoubleOrNull() ?: 0.0
                val price = s.amount.toDoubleOrNull() ?: 0.0
                qty * price
            }
            AssetSubType.PUBLIC_FUND -> {
                val shares = s.quantity.toDoubleOrNull() ?: 0.0
                val nav = s.amount.toDoubleOrNull() ?: 0.0
                shares * nav
            }
            AssetSubType.MORTGAGE, AssetSubType.CAR_LOAN -> {
                s.remainingPrincipal.toDoubleOrNull() ?: s.amount.toDoubleOrNull() ?: 0.0
            }
            AssetSubType.CREDIT_CARD -> {
                s.amount.toDoubleOrNull() ?: 0.0
            }
            AssetSubType.INSURANCE -> {
                s.cashValue.toDoubleOrNull() ?: 0.0
            }
            else -> s.amount.toDoubleOrNull() ?: 0.0
        }
    }

    private fun buildExtraJson(subType: AssetSubType, s: AddAssetState): String? {
        val map = mutableMapOf<String, String>()
        when (subType) {
            AssetSubType.BANK_FINANCIAL, AssetSubType.TIME_DEPOSIT, AssetSubType.LARGE_DEPOSIT -> {
                if (s.rate.isNotBlank()) map["rate"] = s.rate
                if (s.maturityDate.isNotBlank()) map["maturityDate"] = s.maturityDate
            }
            AssetSubType.PROVIDENT_FUND -> {
                if (s.monthlyBase.isNotBlank()) map["monthlyBase"] = s.monthlyBase
                if (s.personalRate.isNotBlank()) map["personalRate"] = s.personalRate
                if (s.companyRate.isNotBlank()) map["companyRate"] = s.companyRate
            }
            AssetSubType.INSURANCE -> {
                if (s.premiumPaid.isNotBlank()) map["premiumPaid"] = s.premiumPaid
                if (s.cashValue.isNotBlank()) map["cashValue"] = s.cashValue
            }
            AssetSubType.MORTGAGE, AssetSubType.CAR_LOAN -> {
                if (s.totalLoan.isNotBlank()) map["totalLoan"] = s.totalLoan
                if (s.remainingPrincipal.isNotBlank()) map["remainingPrincipal"] = s.remainingPrincipal
                if (s.monthlyPayment.isNotBlank()) map["monthlyPayment"] = s.monthlyPayment
                if (s.loanRate.isNotBlank()) map["loanRate"] = s.loanRate
            }
            else -> {}
        }
        return if (map.isEmpty()) null else com.google.gson.Gson().toJson(map)
    }
}
