package com.reco.app.data.remote

import com.google.gson.annotations.SerializedName

data class TrendingResponse(
    @SerializedName("results") val results: List<TrendingItemDto>,
)

data class SearchResponse(
    @SerializedName("results") val results: List<TrendingItemDto>,
)

data class TrendingItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
)

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
)

data class MovieDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("genres") val genres: List<GenreDto>?,
)

data class TvDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>?,
    @SerializedName("genres") val genres: List<GenreDto>?,
)

data class WatchProviderDto(
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("logo_path") val logoPath: String?,
)

data class WatchProviderCountryDto(
    @SerializedName("flatrate") val flatrate: List<WatchProviderDto>?,
    @SerializedName("rent") val rent: List<WatchProviderDto>?,
    @SerializedName("buy") val buy: List<WatchProviderDto>?,
)

data class WatchProvidersResponse(
    @SerializedName("results") val results: Map<String, WatchProviderCountryDto>?,
)
