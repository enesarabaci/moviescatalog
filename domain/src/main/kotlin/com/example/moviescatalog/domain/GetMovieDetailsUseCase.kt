package com.example.moviescatalog.domain

import com.example.moviescatalog.data.repository.MovieRepository
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    operator fun invoke(id: Int): Flow<DataState<MovieData>> {
        return movieRepository.getMovieDetails(id)
            .flowOn(Dispatchers.IO)
    }
}