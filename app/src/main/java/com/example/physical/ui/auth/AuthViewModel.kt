package com.example.physical.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physical.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val error: String? = null,
    val userName: String = ""
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        if (repository.isLoggedIn()) {
            _uiState.value = AuthUiState(
                isLoggedIn = true,
                userName = repository.currentUser?.displayName ?: ""
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState(
                        isLoggedIn = true,
                        userName = user.displayName ?: ""
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.signUp(name, email, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState(
                        isLoggedIn = true,
                        userName = user.displayName ?: name
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Sign up failed"
                    )
                }
            )
        }
    }

    fun continueAsGuest() {
        _uiState.value = AuthUiState(
            isLoggedIn = true,
            isGuest = true,
            userName = "Guest"
        )
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
