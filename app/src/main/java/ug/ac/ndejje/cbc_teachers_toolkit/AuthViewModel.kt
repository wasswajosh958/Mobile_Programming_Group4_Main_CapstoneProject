package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ug.ac.ndejje.cbc_teachers_toolkit.data.AuthRepository
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.UserEntity

enum class AuthMode {
    LOGIN,
    REGISTER
}

data class AuthUiState(
    val isBusy: Boolean = false,
    val mode: AuthMode = AuthMode.LOGIN,
    val fullName: String = "",
    val username: String = "",
    val password: String = "",
    val message: String = ""
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = authRepository.observeCurrentUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null
    )

    init {
        viewModelScope.launch { authRepository.ensureDefaultUser() }
    }

    fun switchMode(mode: AuthMode) {
        _uiState.value = _uiState.value.copy(mode = mode, message = "")
    }

    fun updateFullName(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    fun updateUsername(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun authenticate() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(message = "Username and password are required.")
            return
        }
        if (state.mode == AuthMode.REGISTER && state.fullName.isBlank()) {
            _uiState.value = state.copy(message = "Full name is required for registration.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true, message = "")
            val result = if (_uiState.value.mode == AuthMode.LOGIN) {
                authRepository.login(_uiState.value.username, _uiState.value.password)
            } else {
                authRepository.register(
                    fullName = _uiState.value.fullName,
                    username = _uiState.value.username,
                    password = _uiState.value.password
                )
            }

            _uiState.value = _uiState.value.copy(
                isBusy = false,
                message = result.exceptionOrNull()?.message ?: ""
            )
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    class Factory(
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
