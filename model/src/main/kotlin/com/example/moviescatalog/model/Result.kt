package com.example.moviescatalog.model

sealed class CatalogResult<out T>(open val catalog: MovieCatalog) {

    data class Success<T>(
        override val catalog: MovieCatalog,
        val data: T?
    ) : CatalogResult<T>(catalog)

    data class Error(
        override val catalog: MovieCatalog,
        val message: String? = null
    ) : CatalogResult<Nothing>(catalog)

    data class Loading(
        override val catalog: MovieCatalog
    ) : CatalogResult<Nothing>(catalog)

    data class Idle(
        override val catalog: MovieCatalog
    ) : CatalogResult<Nothing>(catalog)
}