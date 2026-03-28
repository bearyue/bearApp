package com.bear.asset.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bear.asset.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("用户名和密码不能为空")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = authRepository.login(username, password)
            result.fold(
                onSuccess = {
                    _isLoggedIn.value = true
                    _uiState.value = LoginUiState.Success
                },
                onFailure = { e ->
                    _uiState.value = LoginUiState.Error(e.message ?: "登录失败，请重试")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
