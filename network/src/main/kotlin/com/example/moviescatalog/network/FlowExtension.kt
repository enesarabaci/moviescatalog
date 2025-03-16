package com.example.moviescatalog.network

import com.example.moviescatalog.model.ErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.net.UnknownHostException

inline fun <T> Flow<T>.catchException(
    crossinline action: suspend FlowCollector<T>.(errorType: ErrorType) -> Unit
): Flow<T> {
    return catch { throwable ->
        when (throwable) {
            is HttpException, is UnknownHostException -> action(ErrorType.NETWORK)
            else -> action(ErrorType.UNKNOWN)
        }
    }
}