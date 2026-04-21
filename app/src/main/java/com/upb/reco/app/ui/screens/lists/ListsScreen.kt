package com.upb.reco.app.ui.screens.lists

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upb.reco.app.data.model.Movie
import com.upb.reco.app.ui.components.PosterCard
import com.upb.reco.app.ui.components.RecoPrimaryButton
import com.upb.reco.app.ui.components.RecoSecondaryButton
import com.upb.reco.app.ui.components.RecoTextField
import com.upb.reco.app.util.UiState
import kotlinx.coroutines.launch

@Composable
fun ListsScreen(
    viewModel: ListsViewModel,
    onMovieTap: (String, Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var newListName by rememberSaveable { mutableStateOf("") }
    var selectedListForAdd by remember { mutableStateOf<String?>(null) }

    val favorites = (state as? UiState.Success)?.data?.favorites.orEmpty()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (val s = state) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                Text(
                    text = s.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is UiState.Success -> {
                val uiModel = s.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Listas",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = "Hola, ${uiModel.userName}. Gestiona tus favoritos y colecciones personales.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(
                                    text = "Nueva lista",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                RecoTextField(
                                    value = newListName,
                                    onValueChange = { newListName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Ej. Películas pendientes") },
                                )
                                RecoPrimaryButton(
                                    text = "Crear lista",
                                    onClick = {
                                        scope.launch {
                                            val result = viewModel.createList(newListName)
                                            result.fold(
                                                onSuccess = {
                                                    newListName = ""
                                                    snackbarHostState.showSnackbar("Lista creada")
                                                },
                                                onFailure = { error ->
                                                    snackbarHostState.showSnackbar(error.message ?: "No se pudo crear la lista")
                                                },
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Mis favoritos",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    if (uiModel.favorites.isEmpty()) {
                        item {
                            Text(
                                text = "Todavía no agregaste favoritos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(end = 4.dp),
                            ) {
                                items(uiModel.favorites, key = { movieKey(it) }) { movie ->
                                    FavoriteMovieCard(
                                        movie = movie,
                                        onOpen = { onMovieTap(movie.mediaType, movie.id) },
                                        onToggleFavorite = { viewModel.toggleFavorite(movie) },
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Mis listas",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "${uiModel.customLists.size} activas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    if (uiModel.customLists.isEmpty()) {
                        item {
                            Text(
                                text = "Crea tu primera lista para organizar contenido.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(uiModel.customLists, key = { it.name }) { list ->
                            CustomListCard(
                                list = list,
                                onMovieTap = onMovieTap,
                                onAddMovie = { selectedListForAdd = list.name },
                                onRemoveMovie = { movie -> viewModel.removeMovieFromList(list.name, movie) },
                                onDeleteList = { viewModel.deleteList(list.name) },
                            )
                        }
                    }
                }
            }
            UiState.Idle -> Unit
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (selectedListForAdd != null) {
        AddFavoriteToListDialog(
            listName = selectedListForAdd!!,
            favorites = favorites,
            onDismiss = { selectedListForAdd = null },
            onMovieSelected = { movie ->
                val listName = selectedListForAdd!!
                viewModel.addMovieToList(listName, movie)
                selectedListForAdd = null
                scope.launch {
                    snackbarHostState.showSnackbar("Agregado a $listName")
                }
            },
        )
    }
}

private fun movieKey(movie: Movie): String = "${movie.mediaType}-${movie.id}"

@Composable
private fun FavoriteMovieCard(
    movie: Movie,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PosterCard(
            movie = movie,
            onClick = onOpen,
            isFavorite = true,
            onFavoriteClick = onToggleFavorite,
        )
        TextButton(onClick = onToggleFavorite) {
            Text("Quitar")
        }
    }
}

@Composable
private fun CustomListCard(
    list: MovieListUi,
    onMovieTap: (String, Int) -> Unit,
    onAddMovie: () -> Unit,
    onRemoveMovie: (Movie) -> Unit,
    onDeleteList: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text = list.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${list.movies.size} títulos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onAddMovie) {
                        Text("Agregar")
                    }
                    TextButton(onClick = onDeleteList) {
                        Text("Eliminar")
                    }
                }
            }

            if (list.movies.isEmpty()) {
                Text(
                    text = "La lista está vacía.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(list.movies, key = { movieKey(it) }) { movie ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PosterCard(
                                movie = movie,
                                onClick = { onMovieTap(movie.mediaType, movie.id) },
                            )
                            TextButton(onClick = { onRemoveMovie(movie) }) {
                                Text("Quitar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddFavoriteToListDialog(
    listName: String,
    favorites: List<Movie>,
    onDismiss: () -> Unit,
    onMovieSelected: (Movie) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Agregar a $listName") },
        text = {
            if (favorites.isEmpty()) {
                Text(
                    text = "No hay favoritos disponibles para agregar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(favorites, key = { movieKey(it) }) { movie ->
                        ListItem(
                            headlineContent = { Text(movie.title) },
                            supportingContent = { Text(movie.mediaType) },
                            modifier = Modifier.clickable { onMovieSelected(movie) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
