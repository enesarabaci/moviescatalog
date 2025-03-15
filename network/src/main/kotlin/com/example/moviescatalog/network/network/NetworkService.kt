package com.example.moviescatalog.network.network

import com.example.moviescatalog.network.response.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("discover/movie?include_adult=false&include_video=false&language=en-US&page=1")
    suspend fun getMovies(@Query("sort_by") sortBy: String): MovieListResponse
}

