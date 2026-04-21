package com.upb.reco.app.data.repository

import com.upb.reco.app.BuildConfig
import com.upb.reco.app.data.model.Movie
import com.upb.reco.app.data.model.Platform
import com.upb.reco.app.data.remote.MovieDetailDto
import com.upb.reco.app.data.remote.TmdbApi
import com.upb.reco.app.data.remote.TmdbClient
import com.upb.reco.app.data.remote.TrendingItemDto
import com.upb.reco.app.data.remote.TvDetailDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MovieRepository(
    private val api: TmdbApi = TmdbClient.api,
) {

    suspend fun getTrending(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        val key = BuildConfig.TMDB_API_KEY
        if (key.isBlank()) {
            return@withContext Result.success(demoMovies())
        }
        runCatching {
            val response = api.trending(key)
            response.results
                .asSequence()
                .filter { it.mediaType == "movie" || it.mediaType == "tv" }
                .map { mapDto(it) }
                .toList()
        }
    }

    suspend fun search(query: String): Result<List<Movie>> = withContext(Dispatchers.IO) {
        val key = BuildConfig.TMDB_API_KEY
        if (key.isBlank()) {
            return@withContext Result.success(
                demoMovies().filter { it.title.contains(query, ignoreCase = true) },
            )
        }
        if (query.isBlank()) return@withContext Result.success(emptyList())
        runCatching {
            val response = api.search(key, query)
            response.results
                .asSequence()
                .filter { it.mediaType == "movie" || it.mediaType == "tv" }
                .map { mapDto(it) }
                .toList()
        }
    }

    suspend fun getMovieDetail(id: Int): Result<Movie> = withContext(Dispatchers.IO) {
        val key = BuildConfig.TMDB_API_KEY
        if (key.isBlank()) {
            return@withContext Result.success(demoMovies().firstOrNull { it.id == id } ?: demoMovies().first())
        }
        runCatching {
            coroutineScope {
                val detailDeferred = async { api.movieDetail(id, key) }
                val providersDeferred = async {
                    runCatching { api.movieWatchProviders(id, key) }.getOrNull()
                }
                val dto = detailDeferred.await()
                val providers = providersDeferred.await()
                val platforms = resolveProviders(providers) ?: platformsForId(id)
                mapMovieDetail(dto, platforms)
            }
        }
    }

    suspend fun getTvDetail(id: Int): Result<Movie> = withContext(Dispatchers.IO) {
        val key = BuildConfig.TMDB_API_KEY
        if (key.isBlank()) {
            return@withContext Result.success(demoMovies().firstOrNull { it.id == id } ?: demoMovies().first())
        }
        runCatching {
            coroutineScope {
                val detailDeferred = async { api.tvDetail(id, key) }
                val providersDeferred = async {
                    runCatching { api.tvWatchProviders(id, key) }.getOrNull()
                }
                val dto = detailDeferred.await()
                val providers = providersDeferred.await()
                val platforms = resolveProviders(providers) ?: platformsForId(id)
                mapTvDetail(dto, platforms)
            }
        }
    }

    private fun mapDto(dto: TrendingItemDto): Movie {
        val mt = if (dto.mediaType == "tv") "tv" else "movie"
        val displayTitle = dto.title?.takeIf { it.isNotBlank() }
            ?: dto.name?.takeIf { it.isNotBlank() }
            ?: "Sin título"
        val date = dto.releaseDate?.takeIf { it.length >= 4 }
            ?: dto.firstAirDate?.takeIf { it.length >= 4 }
            ?: ""
        val year = date.take(4).ifBlank { "—" }
        val genres = dto.genreIds.orEmpty().mapNotNull { genreNames[it] }.take(3)
        val overviewText = dto.overview?.takeIf { it.isNotBlank() } ?: ""
        val platforms = platformsForId(dto.id)
        return Movie(
            id = dto.id,
            mediaType = mt,
            title = displayTitle,
            year = year,
            runtime = "",
            genres = genres,
            synopsis = overviewText,
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            voteAverage = dto.voteAverage ?: 0.0,
            platforms = platforms,
        )
    }

    private fun mapMovieDetail(dto: MovieDetailDto, platforms: List<Platform>): Movie {
        val date = dto.releaseDate?.takeIf { it.length >= 4 } ?: ""
        val runtimeText = dto.runtime?.takeIf { it > 0 }?.let { minutesToText(it) } ?: ""
        val genres = dto.genres.orEmpty().map { it.name }.take(3)
        return Movie(
            id = dto.id,
            mediaType = "movie",
            title = dto.title?.takeIf { it.isNotBlank() } ?: "Sin título",
            year = date.take(4).ifBlank { "—" },
            runtime = runtimeText,
            genres = genres,
            synopsis = dto.overview.orEmpty(),
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            voteAverage = dto.voteAverage ?: 0.0,
            platforms = platforms,
        )
    }

    private fun mapTvDetail(dto: TvDetailDto, platforms: List<Platform>): Movie {
        val date = dto.firstAirDate?.takeIf { it.length >= 4 } ?: ""
        val runtimeMinutes = dto.episodeRunTime.orEmpty().firstOrNull() ?: 0
        val runtimeText = runtimeMinutes.takeIf { it > 0 }?.let { minutesToText(it) } ?: ""
        val genres = dto.genres.orEmpty().map { it.name }.take(3)
        return Movie(
            id = dto.id,
            mediaType = "tv",
            title = dto.name?.takeIf { it.isNotBlank() } ?: "Sin título",
            year = date.take(4).ifBlank { "—" },
            runtime = runtimeText,
            genres = genres,
            synopsis = dto.overview.orEmpty(),
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            voteAverage = dto.voteAverage ?: 0.0,
            platforms = platforms,
        )
    }

    private fun minutesToText(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) "$hours h ${minutes}m" else "${minutes}m"
    }

    /**
     * Maps a WatchProvidersResponse to known Platform entries using TMDb provider IDs.
     * Checks ES first, then MX, then AR. Returns null if no match is found so callers
     * can fall back to platformsForId().
     */
    private fun resolveProviders(response: com.upb.reco.app.data.remote.WatchProvidersResponse?): List<Platform>? {
        val results = response?.results ?: return null
        val countryData = results["ES"] ?: results["MX"] ?: results["AR"] ?: return null
        val providerIds = (countryData.flatrate.orEmpty() +
                countryData.rent.orEmpty() +
                countryData.buy.orEmpty())
            .map { it.providerId }
            .distinct()
        val platforms = providerIds.mapNotNull { tmdbProviderToPlatform[it] }.distinct().take(3)
        return platforms.ifEmpty { null }
    }

    /** Deterministic fallback when watch/providers returns no data for our regions. */
    private fun platformsForId(id: Int): List<Platform> {
        val all = Platform.entries.toList()
        val idx = abs(id) % all.size
        return listOf(all[idx], all[(idx + 2) % all.size])
    }

    companion object {
        /**
         * TMDb watch-provider IDs → Platform.
         * Source: https://www.themoviedb.org/talk/5f54a25c4de2e50040d4b9d8
         */
        private val tmdbProviderToPlatform: Map<Int, Platform> = mapOf(
            8 to Platform.NETFLIX,
            337 to Platform.DISNEY_PLUS,
            390 to Platform.DISNEY_PLUS,
            // HBO Max / Max
            384 to Platform.HBO_MAX,
            1899 to Platform.HBO_MAX,
            // Prime Video
            9 to Platform.PRIME_VIDEO,
            119 to Platform.PRIME_VIDEO,
            // Apple TV+
            350 to Platform.APPLE_TV,
            2 to Platform.APPLE_TV,
        )

        private val genreNames: Map<Int, String> = mapOf(
            28 to "Acción",
            12 to "Aventura",
            16 to "Animación",
            35 to "Comedia",
            80 to "Crimen",
            99 to "Documental",
            18 to "Drama",
            10751 to "Familia",
            14 to "Fantasía",
            36 to "Historia",
            27 to "Terror",
            10402 to "Música",
            9648 to "Misterio",
            10749 to "Romance",
            878 to "Ciencia ficción",
            53 to "Suspense",
            10752 to "Bélica",
            37 to "Western",
        )

        private fun demoMovies(): List<Movie> = listOf(
            Movie(
                id = 101,
                mediaType = "tv",
                title = "The Bear",
                year = "2022",
                runtime = "",
                genres = listOf("Drama", "Comedia"),
                synopsis = "Un chef vuelve a Chicago para salvar el restaurante de la familia.",
                posterPath = null,
                backdropPath = null,
                voteAverage = 8.4,
                platforms = listOf(Platform.DISNEY_PLUS),
            ),
            Movie(
                id = 102,
                mediaType = "tv",
                title = "Fallout",
                year = "2024",
                runtime = "",
                genres = listOf("Ciencia ficción", "Acción"),
                synopsis = "En un mundo postapocalíptico, los supervivientes luchan por el futuro.",
                posterPath = null,
                backdropPath = null,
                voteAverage = 8.1,
                platforms = listOf(Platform.PRIME_VIDEO),
            ),
            Movie(
                id = 103,
                mediaType = "tv",
                title = "Severance",
                year = "2022",
                runtime = "",
                genres = listOf("Ciencia ficción", "Drama"),
                synopsis = "Empleados de una empresa someten su memoria a un procedimiento radical.",
                posterPath = null,
                backdropPath = null,
                voteAverage = 8.7,
                platforms = listOf(Platform.APPLE_TV),
            ),
            Movie(
                id = 104,
                mediaType = "movie",
                title = "Oppenheimer",
                year = "2023",
                runtime = "3h 0m",
                genres = listOf("Drama", "Historia"),
                synopsis = "La historia del físico J. Robert Oppenheimer y la bomba atómica.",
                posterPath = null,
                backdropPath = null,
                voteAverage = 8.3,
                platforms = listOf(Platform.NETFLIX, Platform.APPLE_TV),
            ),
            Movie(
                id = 105,
                mediaType = "movie",
                title = "Arrival",
                year = "2016",
                runtime = "1h 56m",
                genres = listOf("Ciencia ficción", "Drama"),
                synopsis = "Una lingüista intenta comunicarse con alienígenas que llegan a la Tierra.",
                posterPath = null,
                backdropPath = null,
                voteAverage = 7.9,
                platforms = listOf(Platform.PRIME_VIDEO, Platform.NETFLIX),
            ),
            Movie(
                id = 106,
                mediaType = "movie",
                title = "Dune: Parte Dos",
                year = "2024",
                runtime = "2h 46m",
                genres = listOf("Ciencia ficción", "Aventura"),
                synopsis = "Paul Atreides une fuerzas con los Fremen contra sus enemigos.",
                posterPath = null,
                backdropPath = null,
                voteAverage = 8.5,
                platforms = listOf(Platform.NETFLIX, Platform.HBO_MAX),
            ),
        )
    }
}
