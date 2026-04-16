package com.reco.app.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.reco.app.data.model.Movie
import com.reco.app.data.preferences.UserPreferences
import com.reco.app.data.repository.MovieRepository
import com.reco.app.util.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MovieListUi(
    val name: String,
    val movies: List<Movie>,
)

data class ListsUiModel(
    val userName: String = "tú",
    val favorites: List<Movie> = emptyList(),
    val customLists: List<MovieListUi> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class ListsViewModel(
    private val repository: MovieRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    val state: StateFlow<UiState<ListsUiModel>> = combine(
        userPreferences.userName,
        userPreferences.favoritesIds,
        userPreferences.customLists,
    ) { userName, favoritesIds, customLists ->
        Triple(userName, favoritesIds, customLists)
    }.flatMapLatest { (userName, favoritesIds, customLists) ->
        flow {
            emit(UiState.Loading)
            val favorites = loadMovies(favoritesIds.toList())
            val lists = customLists.map { (name, ids) ->
                MovieListUi(
                    name = name,
                    movies = loadMovies(ids.toList()),
                )
            }
            emit(
                UiState.Success(
                    ListsUiModel(
                        userName = userName,
                        favorites = favorites,
                        customLists = lists,
                    ),
                ),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    suspend fun createList(name: String): Result<Unit> = runCatching {
        require(name.trim().isNotBlank()) { "El nombre de la lista no puede estar vacío" }
        userPreferences.createList(name)
    }

    fun deleteList(name: String) {
        viewModelScope.launch {
            userPreferences.deleteList(name)
        }
    }

    fun addMovieToList(listName: String, movie: Movie) {
        viewModelScope.launch {
            userPreferences.addToList(listName, movieKey(movie))
        }
    }

    fun removeMovieFromList(listName: String, movie: Movie) {
        viewModelScope.launch {
            userPreferences.removeFromList(listName, movieKey(movie))
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            userPreferences.toggleFavorite(movie.mediaType, movie.id)
        }
    }

    private suspend fun loadMovies(keys: List<String>): List<Movie> = coroutineScope {
        keys.distinct().mapNotNull { key ->
            async { loadMovie(key) }
        }.mapNotNull { it.await() }
    }

    private suspend fun loadMovie(key: String): Movie? {
        val parts = key.split(":", limit = 2)
        if (parts.size != 2) return null
        val mediaType = parts[0]
        val id = parts[1].toIntOrNull() ?: return null
        val result = when (mediaType) {
            "tv" -> repository.getTvDetail(id)
            else -> repository.getMovieDetail(id)
        }
        return result.getOrNull()
    }

    private fun movieKey(movie: Movie): String = "${movie.mediaType}:${movie.id}"

    companion object {
        fun Factory(
            repository: MovieRepository,
            userPreferences: UserPreferences,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(ListsViewModel::class.java))
                return ListsViewModel(repository, userPreferences) as T
            }
        }
    }
}
