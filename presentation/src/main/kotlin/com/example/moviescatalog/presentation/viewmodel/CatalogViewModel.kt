package com.example.moviescatalog.presentation.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.moviescatalog.domain.usecase.GetMoviesUseCase
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.ErrorType
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CatalogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val movieCatalog = MovieCatalog.entries

    private val _catalogStateFlow = MutableStateFlow<List<CatalogState<PagingData<MovieData>>>>(
        movieCatalog.map { movieCatalog ->
            movieCatalog.idle()
        }
    )

    val catalogStateFlow: StateFlow<List<CatalogState<PagingData<MovieData>>>>
        get() = _catalogStateFlow

    init {
        fetchCatalogs()
    }

    fun fetchCatalogs() {
        val flows = movieCatalog.map { movieCatalog ->
            getMoviesUseCase(
                movieCatalog = movieCatalog,
                cachedInScope = viewModelScope
            )
        }

        viewModelScope.launch {
            combine(*flows.toTypedArray()) { resultArray ->
                resultArray.toList()
            }.collectLatest { resultList ->
                _catalogStateFlow.value = resultList
            }
        }
    }

    private val innerRecyclerViewSavedStates = mutableMapOf<Int, Parcelable?>()

    fun updateInnerRecyclerViewSavedState(
        position: Int,
        savedState: Parcelable?
    ) {
        innerRecyclerViewSavedStates[position] = savedState
        savedStateHandle[KEY_INNER_RECYCLER_VIEW_SAVED_STATES] = innerRecyclerViewSavedStates
    }

    fun getInnerRecyclerViewSavedStates(): Map<Int, Parcelable?> {
        return savedStateHandle[KEY_INNER_RECYCLER_VIEW_SAVED_STATES] ?: mapOf()
    }

    fun showNetworkErrorForCatalog(catalog: MovieCatalog) {
        _catalogStateFlow.update { list ->
            list.map { catalogState ->
                if (catalogState.catalog == catalog) {
                    catalog.error(ErrorType.NETWORK)
                } else {
                    catalogState
                }
            }
        }
    }

    companion object {
        const val KEY_INNER_RECYCLER_VIEW_SAVED_STATES = "KEY_INNER_RECYCLER_VIEW_SAVED_STATES"
    }
}