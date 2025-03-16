package com.example.moviescatalog.model

data class MovieData(
    val id: Int?,
    val title: String?,
    val originalTitle: String?,
    val overview: String?,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String?,
    val voteAverage: Double?
)