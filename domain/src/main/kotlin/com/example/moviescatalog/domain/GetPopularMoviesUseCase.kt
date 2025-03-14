package com.example.moviescatalog.domain

import com.example.moviescatalog.data.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPopularMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    operator fun invoke(): Flow<Nothing> {
        return movieRepository.getPopularMovies().map {
            it
        }
    }
}