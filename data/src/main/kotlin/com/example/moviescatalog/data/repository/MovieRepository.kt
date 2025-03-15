package com.example.moviescatalog.data.repository

import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieListData
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getMovies(movieCatalog: MovieCatalog): Flow<CatalogState<MovieListData>>
}