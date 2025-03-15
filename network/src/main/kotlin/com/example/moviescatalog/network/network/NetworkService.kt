package com.example.moviescatalog.network.network

import com.example.moviescatalog.network.response.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("discover/movie")
    suspend fun getMovies(
        @Query("sort_by") sortBy: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListResponse
}