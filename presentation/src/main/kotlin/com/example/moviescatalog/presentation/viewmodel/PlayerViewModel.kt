package com.example.moviescatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviescatalog.domain.GetMovieDetailsUseCase
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.presentation.view.player.VideoData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _videoDataState = MutableStateFlow(
        VideoData(
            id = "id",
            drmScheme = VideoData.DrmScheme.Widevine,
            url = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd",
            licenseUrl = "https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test"
        )
    )

    val videoDataState: StateFlow<VideoData>
        get() = _videoDataState

    private val _movieDetailsStateFlow = MutableStateFlow<DataState<MovieData>>(DataState.Idle)
    val movieDetailsStateFlow: StateFlow<DataState<MovieData>>
        get() = _movieDetailsStateFlow

    fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            getMovieDetailsUseCase(id).collectLatest { result ->
                _movieDetailsStateFlow.value = result
            }
        }
    }
}