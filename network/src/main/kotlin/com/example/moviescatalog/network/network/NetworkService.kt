package com.example.moviescatalog.network.network

import com.example.moviescatalog.network.response.MovieListResponse
import com.example.moviescatalog.network.response.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse
}