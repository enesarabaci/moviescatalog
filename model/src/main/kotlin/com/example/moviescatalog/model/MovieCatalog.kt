package com.example.moviescatalog.model

enum class MovieCatalog(
    val sortByQuery: String
) {
    POPULAR("popular.desc"),
    TOP_RATED("vote_average.desc"),
    REVENUE("revenue.desc"),
    RELEASE_DATE("primary_release_date.desc");

    fun idle() = CatalogResult.Idle(this)
    fun loading() = CatalogResult.Loading(this)
    fun error(message: String?) = CatalogResult.Error(this, message)
    fun <T> success(data: T) = CatalogResult.Success(this, data)
}