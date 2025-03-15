package com.example.moviescatalog.model

sealed class CatalogState<out T>(open val catalog: MovieCatalog) {

    data class Success<T>(
        override val catalog: MovieCatalog,
        val data: T?
    ) : CatalogState<T>(catalog)

    data class Error(
        override val catalog: MovieCatalog,
        val message: String? = null
    ) : CatalogState<Nothing>(catalog)

    data class Loading(
        override val catalog: MovieCatalog
    ) : CatalogState<Nothing>(catalog)

    data class Idle(
        override val catalog: MovieCatalog
    ) : CatalogState<Nothing>(catalog)
}