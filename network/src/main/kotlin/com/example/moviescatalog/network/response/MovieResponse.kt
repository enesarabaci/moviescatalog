package com.example.moviescatalog.network.response

import com.example.moviescatalog.model.MovieData
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("adult")
    private val adult: Boolean?,
    @SerializedName("backdrop_path")
    private val backdropPath: String?,
    @SerializedName("genre_ids")
    private val genreIds: List<Int>?,
    @SerializedName("id")
    private val id: Int?,
    @SerializedName("original_language")
    private val originalLanguage: String?,
    @SerializedName("original_title")
    private val originalTitle: String?,
    @SerializedName("overview")
    private val overview: String?,
    @SerializedName("popularity")
    private val popularity: Double?,
    @SerializedName("poster_path")
    private val posterPath: String?,
    @SerializedName("release_date")
    private val releaseDate: String?,
    @SerializedName("title")
    private val title: String?,
    @SerializedName("video")
    private val video: Boolean?,
    @SerializedName("vote_average")
    private val voteAverage: Double?,
    @SerializedName("vote_count")
    private val voteCount: Int?
) {

    fun toMovieData(): MovieData {
        return MovieData(
            id = id,
            title = title,
            originalTitle = originalTitle,
            overview = overview,
            posterUrl = if (posterPath == null) null else "https://image.tmdb.org/t/p/w500$posterPath",
            backdropUrl = if (backdropPath == null) null else "https://image.tmdb.org/t/p/w500$backdropPath",
            releaseDate = releaseDate,
            voteAverage = voteAverage
        )
    }
}