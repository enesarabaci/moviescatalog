package com.example.moviescatalog.domain

import com.example.moviescatalog.data.repository.MovieRepository
import com.example.moviescatalog.model.CatalogResult
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    operator fun invoke(movieCatalog: MovieCatalog): Flow<CatalogResult<MovieListData>> {
        return movieRepository.getMovies(movieCatalog)
            .flowOn(Dispatchers.IO)
    }
}