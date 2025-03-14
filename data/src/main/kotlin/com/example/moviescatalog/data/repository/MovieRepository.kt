package com.example.moviescatalog.data.repository

import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getPopularMovies(): Flow<Nothing>
}