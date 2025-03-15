package com.example.moviescatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviescatalog.domain.GetMoviesUseCase
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieListData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CatalogViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val movieCatalog = MovieCatalog.entries

    private val _catalogStateFlow = MutableStateFlow<List<CatalogState<MovieListData>>>(
        movieCatalog.map { movieCatalog ->
            movieCatalog.idle()
        }
    )

    val catalogStateFlow: StateFlow<List<CatalogState<MovieListData>>>
        get() = _catalogStateFlow

    private val flows by lazy {
        movieCatalog.map { movieCatalog ->
            getMoviesUseCase(movieCatalog)
        }
    }

    fun getMovies() {
        viewModelScope.launch {
            combine(*flows.toTypedArray()) { resultArray ->
                resultArray.toList()
            }.collectLatest { resultList ->
                _catalogStateFlow.value = resultList
            }
        }
    }
}