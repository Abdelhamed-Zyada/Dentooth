package com.example.dentooth.core.auth.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.auth.data.AuthRepository
import com.example.dentooth.core.auth.domain.UserSession
import com.example.dentooth.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<UiState<UserSession>>(UiState.Idle)
    val uiState: StateFlow<UiState<UserSession>> = _uiState.asStateFlow()

    fun signIn(email: String, pass: String) {
        if (email.isBlank()) {
            _uiState.value = UiState.Error("Please enter your email address")
            return
        }

        screenModelScope.launch {
            _uiState.value = UiState.Loading
            val result = authRepository.signIn(email, pass)
            result.onSuccess { session ->
                _uiState.value = UiState.Success(session)
            }.onError { message, _ ->
                _uiState.value = UiState.Error(message)
            }
        }
    }

    fun clearError() {
        if (_uiState.value is UiState.Error) {
            _uiState.value = UiState.Idle
        }
    }
}
