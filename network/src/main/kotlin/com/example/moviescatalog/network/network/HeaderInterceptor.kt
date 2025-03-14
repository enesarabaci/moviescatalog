package com.example.moviescatalog.network.network

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxMTIyMjJiNDZhMTA2ZjkxOWYwODdiMTVmZjkxMzI3ZiIsIm5iZiI6MTc0MTk0NDA0NC4xODUsInN1YiI6IjY3ZDNmNGVjYTJiMThkYmVkZDY0ZWNhNyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.dBuTy3KLqqPdc149Bo0Ex-pOOcGBzil2kYnPiIlQNeA")
            .build()

        return chain.proceed(request)
    }
}