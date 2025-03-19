package com.example.moviescatalog.domain.usecase

import com.example.moviescatalog.data.repository.MovieRepository
import com.example.moviescatalog.domain.di.IoDispatcher
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(id: Int): Flow<DataState<MovieData>> {
        return movieRepository.getMovieDetails(id)
            .flowOn(ioDispatcher)
    }
}