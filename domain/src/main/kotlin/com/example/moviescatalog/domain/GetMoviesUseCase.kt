package com.example.moviescatalog.domain

import com.example.moviescatalog.data.repository.MovieRepository
import com.example.moviescatalog.model.MovieData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    operator fun invoke(sortedBy: String): Flow<List<MovieData>> {
        return movieRepository.getMovies(sortedBy).map { response ->
            response.toMovieDataList()
        }
    }
}