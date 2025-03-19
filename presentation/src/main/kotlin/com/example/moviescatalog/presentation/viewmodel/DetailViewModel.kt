package com.example.moviescatalog.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviescatalog.domain.usecase.GetMovieDetailsUseCase
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _movieDetailsStateFlow = MutableStateFlow<DataState<MovieData>>(DataState.Idle)
    val movieDetailsStateFlow: StateFlow<DataState<MovieData>>
        get() = _movieDetailsStateFlow

    private fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            getMovieDetailsUseCase(id).collectLatest { result ->
                _movieDetailsStateFlow.value = result
            }
        }
    }

    init {
        savedStateHandle.get<Int>("id")?.let { id ->
            getMovieDetails(id)
        }
    }
}