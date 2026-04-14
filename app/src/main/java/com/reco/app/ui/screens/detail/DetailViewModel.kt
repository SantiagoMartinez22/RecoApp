package com.reco.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.reco.app.data.model.Movie
import com.reco.app.data.repository.MovieRepository
import com.reco.app.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: MovieRepository,
    private val mediaType: String,
    private val id: Int,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Movie>>(UiState.Loading)
    val state: StateFlow<UiState<Movie>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = if (mediaType == "tv") {
                repository.getTvDetail(id)
            } else {
                repository.getMovieDetail(id)
            }
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "No se pudo cargar el detalle") },
            )
        }
    }

    companion object {
        fun Factory(
            repository: MovieRepository,
            mediaType: String,
            id: Int,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(DetailViewModel::class.java))
                return DetailViewModel(repository, mediaType, id) as T
            }
        }
    }
}
