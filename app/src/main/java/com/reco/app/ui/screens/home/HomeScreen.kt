package com.reco.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reco.app.data.model.Movie
import com.reco.app.data.model.Platform
import com.reco.app.ui.components.GenreChip
import com.reco.app.ui.components.HeroCard
import com.reco.app.ui.components.PosterCard
import com.reco.app.ui.components.SectionHeader
import com.reco.app.util.UiState

private data class PlatformFilter(
    val platform: Platform?,
    val label: String,
)

private val platformFilters = listOf(
    PlatformFilter(null, "Todas"),
    PlatformFilter(Platform.NETFLIX, "Netflix"),
    PlatformFilter(Platform.DISNEY_PLUS, "Disney+"),
    PlatformFilter(Platform.HBO_MAX, "HBO"),
    PlatformFilter(Platform.PRIME_VIDEO, "Prime"),
    PlatformFilter(Platform.APPLE_TV, "Apple TV+"),
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieTap: (mediaType: String, id: Int) -> Unit,
    onSeeAllTrending: () -> Unit,
    onSeeAllByGenre: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (val s = state) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = { viewModel.load() }) {
                        Text("Reintentar")
                    }
                }
            }
            is UiState.Success -> {
                val data = s.data
                HomeContent(
                    data = data,
                    selectedPlatform = selectedPlatform,
                    onSelectPlatform = viewModel::selectPlatform,
                    onMovieTap = onMovieTap,
                    onSeeAllTrending = onSeeAllTrending,
                    onSeeAllByGenre = onSeeAllByGenre,
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun HomeContent(
    data: HomeUiModel,
    selectedPlatform: Platform?,
    onSelectPlatform: (Platform?) -> Unit,
    onMovieTap: (String, Int) -> Unit,
    onSeeAllTrending: () -> Unit,
    onSeeAllByGenre: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Hola, ${data.userName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Para ti hoy",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(platformFilters) { filter ->
                    val selected = selectedPlatform == filter.platform
                    GenreChip(
                        label = filter.label,
                        selected = selected,
                        onClick = { onSelectPlatform(filter.platform) },
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        data.hero?.let { hero ->
            item {
                HeroCard(
                    movie = hero,
                    onClick = { onMovieTap(hero.mediaType, hero.id) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            item { Spacer(modifier = Modifier.height(28.dp)) }
        }

        item {
            SectionHeader(
                title = "Tendencias",
                onSeeAll = onSeeAllTrending,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(data.trendingRow, key = { it.id }) { movie ->
                    PosterCard(
                        movie = movie,
                        onClick = { onMovieTap(movie.mediaType, movie.id) },
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }

        item {
            SectionHeader(
                title = "Por tus géneros",
                onSeeAll = onSeeAllByGenre,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(data.byGenreRow, key = { "g-${it.id}" }) { movie ->
                    PosterCard(
                        movie = movie,
                        onClick = { onMovieTap(movie.mediaType, movie.id) },
                    )
                }
            }
        }

        if (data.hero == null && data.trendingRow.isEmpty() && data.byGenreRow.isEmpty()) {
            item {
                Text(
                    text = "No hay títulos para este filtro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp),
                )
            }
        }
    }
}
