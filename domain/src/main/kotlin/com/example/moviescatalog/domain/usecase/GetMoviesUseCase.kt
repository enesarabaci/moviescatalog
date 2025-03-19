package com.example.moviescatalog.domain.usecase

import com.example.moviescatalog.data.repository.MovieRepository
import com.example.moviescatalog.domain.di.IoDispatcher
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieListData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(movieCatalog: MovieCatalog): Flow<CatalogState<MovieListData>> {
        return movieRepository.getMovies(movieCatalog)
            .flowOn(ioDispatcher)
    }
}