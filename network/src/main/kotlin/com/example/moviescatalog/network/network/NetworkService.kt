package com.example.moviescatalog.network.network

import retrofit2.http.GET

interface NetworkService {

    @GET("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc")
    suspend fun getPopularMovies()
}