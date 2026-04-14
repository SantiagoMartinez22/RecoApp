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
