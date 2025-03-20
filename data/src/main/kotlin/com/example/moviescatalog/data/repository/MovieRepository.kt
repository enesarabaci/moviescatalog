package com.example.moviescatalog.data.repository

import androidx.paging.PagingData
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getMovies(
        movieCatalog: MovieCatalog,
        cachedInScope: CoroutineScope
    ): Flow<CatalogState<PagingData<MovieData>>>

    fun getMovieDetails(id: Int): Flow<DataState<MovieData>>
}