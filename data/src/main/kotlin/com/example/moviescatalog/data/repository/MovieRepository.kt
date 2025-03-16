package com.example.moviescatalog.data.repository

import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.model.MovieListData
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getMovies(movieCatalog: MovieCatalog): Flow<CatalogState<MovieListData>>

    fun getMovieDetails(id: Int): Flow<DataState<MovieData>>
}