package com.example.moviescatalog.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.moviescatalog.data.CatalogPagingSource
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.network.catchException
import com.example.moviescatalog.network.network.NetworkService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val networkService: NetworkService
) : MovieRepository {

    override fun getMovies(
        movieCatalog: MovieCatalog,
        cachedInScope: CoroutineScope
    ): Flow<CatalogState<PagingData<MovieData>>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CatalogPagingSource(
                    networkService = networkService,
                    movieCatalog = movieCatalog
                )
            }
        ).flow.cachedIn(
            cachedInScope
        ).map<PagingData<MovieData>, CatalogState<PagingData<MovieData>>> {
            movieCatalog.success(it)
        }.onStart {
            emit(movieCatalog.loading())
        }.catchException { errorType ->
            emit(movieCatalog.error(errorType))
        }
    }

    override fun getMovieDetails(id: Int): Flow<DataState<MovieData>> {
        return flow<DataState<MovieData>> {
            val response = networkService.getMovieDetails(id)
            emit(DataState.Success(response.toMovieData()))
        }.onStart {
            emit(DataState.Loading)
        }.catchException { errorType ->
            emit(DataState.Error(errorType))
        }
    }
}