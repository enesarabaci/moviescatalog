package com.example.moviescatalog.model

sealed class DataState<out T> {
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val errorType: ErrorType) : DataState<Nothing>()
    data object Loading : DataState<Nothing>()
    data object Idle : DataState<Nothing>()
}

sealed class CatalogState<out T>(open val catalog: MovieCatalog) {

    data class Success<T>(
        override val catalog: MovieCatalog,
        val data: T
    ) : CatalogState<T>(catalog)

    data class Error(
        override val catalog: MovieCatalog,
        val errorType: ErrorType
    ) : CatalogState<Nothing>(catalog)

    data class Loading(
        override val catalog: MovieCatalog
    ) : CatalogState<Nothing>(catalog)

    data class Idle(
        override val catalog: MovieCatalog
    ) : CatalogState<Nothing>(catalog)
}

enum class ErrorType {
    NETWORK,
    UNKNOWN
}