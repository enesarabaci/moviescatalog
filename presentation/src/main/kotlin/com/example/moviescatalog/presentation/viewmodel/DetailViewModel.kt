package com.example.moviescatalog.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviescatalog.domain.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    init {
        savedStateHandle.get<Int>("id")?.let { id ->
            getMovieDetails(id)
        }
    }

    private fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            getMovieDetailsUseCase(id).collectLatest {

            }
        }
    }
}