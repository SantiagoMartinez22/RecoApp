package com.reco.app.data.model

data class Movie(
    val id: Int,
    val mediaType: String,
    val title: String,
    val year: String,
    val runtime: String,
    val genres: List<String>,
    val synopsis: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val platforms: List<Platform>,
)
