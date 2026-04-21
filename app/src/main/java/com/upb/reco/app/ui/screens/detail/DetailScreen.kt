package com.upb.reco.app.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.upb.reco.app.data.model.Movie
import com.upb.reco.app.data.remote.TmdbImageUrls
import com.upb.reco.app.ui.components.PlatformBadge
import com.upb.reco.app.ui.components.RatingPill
import com.upb.reco.app.ui.components.RecoPrimaryButton
import com.upb.reco.app.ui.components.RecoSecondaryButton
import com.upb.reco.app.util.UiState
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val customListNames by viewModel.customListNames.collectAsStateWithLifecycle()
    var showListDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val s = state) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error -> {
                    Text(
                        text = s.message,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                is UiState.Success -> {
                    DetailContent(
                        movie = s.data,
                        onBack = onBack,
                        isFavorite = isFavorite,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onSaveToList = { showListDialog = true },
                    )
                }
                UiState.Idle -> Unit
            }
        }
    }

    if (showListDialog) {
        AlertDialog(
            onDismissRequest = { showListDialog = false },
            title = { Text(text = "Guardar en lista") },
            text = {
                if (customListNames.isEmpty()) {
                    Text(
                        text = "Crea primero una lista en la sección Listas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(customListNames, key = { it }) { listName ->
                            ListItem(
                                headlineContent = { Text(listName) },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.addToList(listName)
                                        showListDialog = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Guardado en $listName")
                                        }
                                    },
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showListDialog = false }) {
                    Text("Cerrar")
                }
            },
        )
    }
}

@Composable
private fun DetailContent(
    movie: Movie,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSaveToList: () -> Unit,
) {
    val context = LocalContext.current
    val backdrop = TmdbImageUrls.backdropUrl(movie.backdropPath)
    val poster = TmdbImageUrls.posterUrl(movie.posterPath)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    if (backdrop != null) {
                        AsyncImage(
                            model = backdrop,
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f),
                                    ),
                                ),
                            ),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 120.dp, height = 180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        if (poster != null) {
                            AsyncImage(
                                model = poster,
                                contentDescription = movie.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize(),
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = listOf(movie.year, movie.runtime, movie.genres.firstOrNull().orEmpty())
                                .filter { it.isNotBlank() }
                                .joinToString(" · "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            movie.platforms.take(3).forEach { platform ->
                                PlatformBadge(platform = platform)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RatingPill(text = "IMDb %.1f".format(movie.voteAverage))
                    RatingPill(text = "RT ${((movie.voteAverage * 10).toInt()).coerceIn(0, 100)}%")
                    RatingPill(text = "RECO %.1f".format((movie.voteAverage + 0.4).coerceAtMost(10.0)), isAccent = true)
                }
            }

            item {
                RecoPrimaryButton(
                    text = "▶ Ver trailer",
                    onClick = {
                        val urlQuery = Uri.encode("${movie.title} trailer")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$urlQuery"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                RecoSecondaryButton(
                    text = "Guardar en lista",
                    onClick = onSaveToList,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            item {
                Text(
                    text = "Sinopsis",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),
                )
                Text(
                    text = movie.synopsis.ifBlank { "No hay sinopsis disponible." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                )
            }

            item {
                Text(
                    text = "Reseñas",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 10.dp),
                )
            }

            items(2) { index ->
                val author = if (index == 0) "María G." else "Carlos R."
                val body = if (index == 0) {
                    "Excelente ritmo y actuaciones sólidas. Muy recomendable."
                } else {
                    "La dirección visual está increíble y engancha desde el inicio."
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp),
                ) {
                    Text(
                        text = author,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "★★★★★",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 10.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 10.dp),
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
