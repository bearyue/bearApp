package com.bear.asset.ui.screen

import androidx.lifecycle.ViewModel
import com.bear.asset.data.local.TokenManager
import com.bear.asset.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val nickname: String = "",
    val username: String = "",
    val biometricEnabled: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        _uiState.value = SettingsUiState(
            nickname = tokenManager.getNickname() ?: "",
            username = tokenManager.getUsername() ?: "",
            biometricEnabled = tokenManager.isBiometricEnabled()
        )
    }

    fun setBiometricEnabled(enabled: Boolean) {
        tokenManager.setBiometricEnabled(enabled)
        _uiState.value = _uiState.value.copy(biometricEnabled = enabled)
    }

    fun logout() {
        authRepository.logout()
    }
}
