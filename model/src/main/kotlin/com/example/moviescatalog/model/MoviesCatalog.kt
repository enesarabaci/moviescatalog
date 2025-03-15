package com.example.moviescatalog.model

enum class MoviesCatalog(
    val sortByQuery: String
) {
    POPULAR("popular.desc"),
    TOP_RATED("vote_average.desc"),
    REVENUE("revenue.desc"),
    RELEASE_DATE("primary_release_date.desc")
}