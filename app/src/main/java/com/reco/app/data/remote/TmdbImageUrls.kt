package com.reco.app.data.remote

object TmdbImageUrls {
    private const val POSTER = "https://image.tmdb.org/t/p/w500"
    private const val BACKDROP = "https://image.tmdb.org/t/p/w780"

    fun posterUrl(path: String?): String? = path?.takeIf { it.isNotBlank() }?.let { "$POSTER$it" }

    fun backdropUrl(path: String?): String? = path?.takeIf { it.isNotBlank() }?.let { "$BACKDROP$it" }
}
