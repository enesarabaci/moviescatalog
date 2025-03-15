package com.example.moviescatalog.network.response

import com.example.moviescatalog.model.MovieListData
import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    @SerializedName("results")
    private val results: List<MovieResponse>?
) {

    fun toMovieListData(): MovieListData {
        return MovieListData(
            movies = results?.map { it.toMovieData() } ?: emptyList()
        )
    }
}