package com.reco.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("trending/all/week")
    suspend fun trending(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
    ): TrendingResponse

    @GET("search/multi")
    suspend fun search(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "es-ES",
    ): SearchResponse

    @GET("movie/{id}")
    suspend fun movieDetail(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
    ): MovieDetailDto

    @GET("tv/{id}")
    suspend fun tvDetail(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
    ): TvDetailDto
}
