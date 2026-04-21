package com.upb.reco.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.upb.reco.app.data.preferences.UserPreferences
import com.upb.reco.app.data.model.Movie
import com.upb.reco.app.data.repository.MovieRepository
import com.upb.reco.app.util.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val repository: MovieRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre

    val favoritesIds: StateFlow<Set<String>> = userPreferences.favoritesIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptySet(),
    )

    val state: StateFlow<UiState<List<Movie>>> = combine(
        _query.debounce(400),
        _selectedGenre,
    ) { query, selectedGenre -> query.trim() to selectedGenre }
        .flatMapLatest { (query, genre) ->
            flow {
                emit(UiState.Loading)
                val result = if (query.isBlank()) repository.getTrending() else repository.search(query)
                emit(
                    result.fold(
                        onSuccess = { movies ->
                            val filtered = if (genre == null) {
                                movies
                            } else {
                                movies.filter { genre in it.genres }
                            }
                            UiState.Success(filtered)
                        },
                        onFailure = {
                            UiState.Error(it.message ?: "No se pudo buscar contenido")
                        },
                    ),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    fun updateQuery(value: String) {
        _query.update { value }
    }

    fun selectGenre(genre: String?) {
        _selectedGenre.update { genre }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            userPreferences.toggleFavorite(movie.mediaType, movie.id)
        }
    }

    companion object {
        val genres = listOf("Acción", "Drama", "Comedia", "Ciencia ficción", "Terror")

        fun Factory(
            repository: MovieRepository,
            userPreferences: UserPreferences,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(SearchViewModel::class.java))
                return SearchViewModel(repository, userPreferences) as T
            }
        }
    }
}
