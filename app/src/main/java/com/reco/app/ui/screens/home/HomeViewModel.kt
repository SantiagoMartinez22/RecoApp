package com.reco.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.reco.app.data.model.Movie
import com.reco.app.data.model.Platform
import com.reco.app.data.repository.MovieRepository
import com.reco.app.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiModel(
    val userName: String = "Ana",
    val hero: Movie?,
    val trendingRow: List<Movie>,
    val byGenreRow: List<Movie>,
)

class HomeViewModel(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _loading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    private val _selectedPlatform = MutableStateFlow<Platform?>(null)

    val selectedPlatform: StateFlow<Platform?> = _selectedPlatform

    val state: StateFlow<UiState<HomeUiModel>> = combine(
        _loading,
        _error,
        _movies,
        _selectedPlatform,
    ) { loading, err, movies, platform ->
        when {
            loading -> UiState.Loading
            err != null -> UiState.Error(err)
            else -> UiState.Success(buildUiModel(movies, platform))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.getTrending().fold(
                onSuccess = {
                    _movies.value = it
                    _loading.value = false
                },
                onFailure = {
                    _error.value = it.message ?: "No se pudieron cargar las recomendaciones"
                    _loading.value = false
                },
            )
        }
    }

    fun selectPlatform(platform: Platform?) {
        _selectedPlatform.update { platform }
    }

    private fun buildUiModel(movies: List<Movie>, platform: Platform?): HomeUiModel {
        val filtered = when (platform) {
            null -> movies
            else -> movies.filter { platform in it.platforms }
        }
        if (filtered.isEmpty()) {
            return HomeUiModel(
                hero = null,
                trendingRow = emptyList(),
                byGenreRow = emptyList(),
            )
        }
        val hero = filtered.first()
        val rest = filtered.drop(1)
        val trending = rest.take(10)
        val byGenre = filtered.takeLast(minOf(5, filtered.size))
        return HomeUiModel(
            hero = hero,
            trendingRow = trending,
            byGenreRow = byGenre,
        )
    }

    companion object {
        fun Factory(repository: MovieRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(HomeViewModel::class.java))
                return HomeViewModel(repository) as T
            }
        }
    }
}
