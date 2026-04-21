package com.upb.reco.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.upb.reco.app.data.repository.AuthRepository
import com.upb.reco.app.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage.asStateFlow()

    fun consumeInfoMessage() {
        _infoMessage.value = null
    }

    fun updateEmail(value: String) {
        _email.update { value }
    }

    fun updatePassword(value: String) {
        _password.update { value }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
        _infoMessage.value = null
    }

    fun signInWithEmail() {
        val e = _email.value.trim()
        val p = _password.value
        if (e.isBlank() || p.isBlank()) {
            _uiState.value = UiState.Error("Completa correo y contraseña")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            _uiState.value = UiState.Error("Correo no válido")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.signInWithEmailAndPassword(e, p)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "No se pudo iniciar sesión") },
            )
        }
    }

    /** Demo sin Firebase (sin Play Services). */
    fun signInWithGoogleDemo() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.signInWithGoogleDemo()
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Error con Google") },
            )
        }
    }

    fun sendPasswordReset() {
        val e = _email.value.trim()
        if (e.isBlank()) {
            _uiState.value = UiState.Error("Introduce tu correo para recuperar la contraseña")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            _uiState.value = UiState.Error("Correo no válido")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.sendPasswordResetEmail(e)
            result.fold(
                onSuccess = {
                    _uiState.value = UiState.Idle
                    _infoMessage.value = "Revisa tu correo para restablecer la contraseña"
                },
                onFailure = {
                    _uiState.value = UiState.Error(it.message ?: "No se pudo enviar el correo")
                },
            )
        }
    }

    companion object {
        fun Factory(repository: AuthRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(LoginViewModel::class.java))
                return LoginViewModel(repository) as T
            }
        }
    }
}
