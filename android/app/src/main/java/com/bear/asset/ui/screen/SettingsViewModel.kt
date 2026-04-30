package com.bear.asset.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bear.asset.data.local.TokenManager
import com.bear.asset.data.repository.AuthRepository
import com.bear.asset.data.repository.ExchangeRateItem
import com.bear.asset.data.repository.ExchangeRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val nickname: String = "",
    val username: String = "",
    val biometricEnabled: Boolean = false,
    val exchangeRates: List<ExchangeRateItem> = emptyList(),
    val exchangeRateSourceDate: String = "",
    val exchangeRateCachedDate: String = "",
    val exchangeRateLoading: Boolean = false,
    val exchangeRateError: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
        loadExchangeRates()
    }

    private fun loadUserInfo() {
        _uiState.value = _uiState.value.copy(
            nickname = tokenManager.getNickname() ?: "",
            username = tokenManager.getUsername() ?: "",
            biometricEnabled = tokenManager.isBiometricEnabled()
        )
    }

    private fun loadExchangeRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                exchangeRateLoading = true,
                exchangeRateError = null
            )
            try {
                val snapshot = exchangeRateRepository.getDailyRates()
                _uiState.value = _uiState.value.copy(
                    exchangeRates = snapshot.rates,
                    exchangeRateSourceDate = snapshot.sourceDate,
                    exchangeRateCachedDate = snapshot.cachedDate,
                    exchangeRateLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    exchangeRateLoading = false,
                    exchangeRateError = "汇率获取失败"
                )
            }
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        tokenManager.setBiometricEnabled(enabled)
        _uiState.value = _uiState.value.copy(biometricEnabled = enabled)
    }

    fun logout() {
        authRepository.logout()
    }
}
