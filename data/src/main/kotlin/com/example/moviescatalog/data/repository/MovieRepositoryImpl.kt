package com.example.moviescatalog.data.repository

import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.model.MovieListData
import com.example.moviescatalog.network.catchException
import com.example.moviescatalog.network.network.NetworkService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val networkService: NetworkService
) : MovieRepository {

    override fun getMovies(movieCatalog: MovieCatalog): Flow<CatalogState<MovieListData>> {
        return flow<CatalogState<MovieListData>> {
            val response = networkService.getMovies(movieCatalog.sortByQuery)
            emit(movieCatalog.success(response.toMovieListData()))
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