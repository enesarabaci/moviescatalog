package com.example.moviescatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviescatalog.domain.GetMoviesUseCase
import com.example.moviescatalog.model.MoviesCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CatalogViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val flows = MoviesCatalog.entries.map { moviesEndpoint ->
        getMoviesUseCase(moviesEndpoint.sortByQuery)
    }

    fun getMovies() {
        viewModelScope.launch {
            merge(*flows.toTypedArray()).collectLatest {

            }
        }
    }
}