package com.example.moviescatalog.network.response

import com.example.moviescatalog.model.MovieData
import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    @SerializedName("results")
    private val results: List<MovieResponse>?
) {

    fun toMovieDataList(): List<MovieData> {
        return results?.map { it.toMovieData() } ?: emptyList()
    }
}