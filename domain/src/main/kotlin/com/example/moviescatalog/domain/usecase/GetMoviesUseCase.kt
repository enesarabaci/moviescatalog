package com.example.moviescatalog.domain.usecase

import androidx.paging.PagingData
import com.example.moviescatalog.data.repository.MovieRepository
import com.example.moviescatalog.domain.di.IoDispatcher
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(
        movieCatalog: MovieCatalog,
        cachedInScope: CoroutineScope
    ): Flow<CatalogState<PagingData<MovieData>>> {
        return movieRepository.getMovies(movieCatalog, cachedInScope)
            .flowOn(ioDispatcher)
    }
}