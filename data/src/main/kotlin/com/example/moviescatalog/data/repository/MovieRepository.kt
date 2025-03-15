package com.example.moviescatalog.data.repository

import com.example.moviescatalog.network.response.MovieListResponse
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getMovies(sortedBy: String): Flow<MovieListResponse>
}