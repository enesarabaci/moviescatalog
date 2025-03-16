package com.example.moviescatalog.model

enum class MovieCatalog(
    val sortByQuery: String
) {
    POPULAR("popular.desc"),
    TOP_RATED("vote_average.desc"),
    REVENUE("revenue.desc"),
    RELEASE_DATE("primary_release_date.desc");

    fun idle() = CatalogState.Idle(this)
    fun loading() = CatalogState.Loading(this)
    fun error(errorType: ErrorType) = CatalogState.Error(this, errorType)
    fun <T> success(data: T) = CatalogState.Success(this, data)
}