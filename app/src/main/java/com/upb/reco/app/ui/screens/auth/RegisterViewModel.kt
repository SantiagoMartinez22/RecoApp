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

class RegisterViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _keywords = MutableStateFlow("")
    val keywords: StateFlow<String> = _keywords.asStateFlow()

    private val _selectedGenres = MutableStateFlow<Set<String>>(emptySet())
    val selectedGenres: StateFlow<Set<String>> = _selectedGenres.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun updateName(value: String) = _name.update { value }
    fun updateEmail(value: String) = _email.update { value }
    fun updatePassword(value: String) = _password.update { value }
    fun updateKeywords(value: String) = _keywords.update { value }

    fun toggleGenre(genre: String) {
        _selectedGenres.update { current ->
            if (genre in current) current - genre else current + genre
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun register() {
        val n = _name.value.trim()
        val e = _email.value.trim()
        val p = _password.value
        val kw = _keywords.value.trim()
        val genres = _selectedGenres.value.toList()

        when {
            n.isBlank() -> _uiState.value = UiState.Error("Introduce tu nombre")
            e.isBlank() -> _uiState.value = UiState.Error("Introduce tu correo")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() ->
                _uiState.value = UiState.Error("Correo no válido")

            p.length < 8 -> _uiState.value = UiState.Error("La contraseña debe tener al menos 8 caracteres")
            genres.isEmpty() -> _uiState.value = UiState.Error("Selecciona al menos un género")
            else -> viewModelScope.launch {
                _uiState.value = UiState.Loading
                val result = repository.registerWithEmailAndPassword(
                    email = e,
                    password = p,
                    name = n,
                    genres = genres,
                    keywords = kw,
                )
                _uiState.value = result.fold(
                    onSuccess = { UiState.Success(Unit) },
                    onFailure = { UiState.Error(it.message ?: "No se pudo crear la cuenta") },
                )
            }
        }
    }

    companion object {
        val GENRES: List<String> = listOf(
            "Acción",
            "Drama",
            "Ciencia ficción",
            "Comedia",
            "Terror",
            "Thriller",
            "Animación",
            "Documental",
        )

        fun Factory(repository: AuthRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(RegisterViewModel::class.java))
                return RegisterViewModel(repository) as T
            }
        }
    }
}
