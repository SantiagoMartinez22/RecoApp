package com.upb.reco.app.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upb.reco.app.ui.components.GenreChip
import com.upb.reco.app.ui.components.PosterCard
import com.upb.reco.app.ui.components.RecoTextField
import com.upb.reco.app.util.UiState

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onMovieTap: (String, Int) -> Unit,
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoritesIds by viewModel.favoritesIds.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = "Buscar",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
        )

        RecoTextField(
            value = query,
            onValueChange = viewModel::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Películas, series, personas...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                )
            },
        )

        LazyRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                GenreChip(
                    label = "Todos",
                    selected = selectedGenre == null,
                    onClick = { viewModel.selectGenre(null) },
                )
            }
            items(SearchViewModel.genres.size) { index ->
                val genre = SearchViewModel.genres[index]
                GenreChip(
                    label = genre,
                    selected = selectedGenre == genre,
                    onClick = { viewModel.selectGenre(genre) },
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp),
        ) {
            when (val s = state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        Text(
                            text = "No se encontraron resultados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(s.data, key = { "${it.mediaType}:${it.id}" }) { movie ->
                                PosterCard(
                                    movie = movie,
                                    onClick = { onMovieTap(movie.mediaType, movie.id) },
                                    isFavorite = "${movie.mediaType}:${movie.id}" in favoritesIds,
                                    onFavoriteClick = { viewModel.toggleFavorite(movie) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
                UiState.Idle -> Unit
            }
        }
    }
}
