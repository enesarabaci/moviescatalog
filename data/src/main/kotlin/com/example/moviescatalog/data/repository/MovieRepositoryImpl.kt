package com.example.moviescatalog.data.repository

import com.example.moviescatalog.network.network.NetworkService
import com.example.moviescatalog.network.response.MovieListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val networkService: NetworkService
) : MovieRepository {

    override fun getMovies(sortedBy: String): Flow<MovieListResponse> {
        return flow {
            emit(networkService.getMovies(sortedBy))
        }
    }
}