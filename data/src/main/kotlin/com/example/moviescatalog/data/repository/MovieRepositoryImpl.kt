package com.example.moviescatalog.data.repository

import com.example.moviescatalog.network.network.NetworkService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val networkService: NetworkService
) : MovieRepository {

    override fun getPopularMovies(): Flow<Nothing> {
        return flow {
            networkService.getPopularMovies()
        }
    }
}