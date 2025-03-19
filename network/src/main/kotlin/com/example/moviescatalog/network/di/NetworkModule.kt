package com.example.moviescatalog.network.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.moviescatalog.network.network.HeaderInterceptor
import com.example.moviescatalog.network.network.NetworkService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.Date
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext applicationContext: Context,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(
                Cache(
                    directory = File(applicationContext.cacheDir, "http_cache"),
                    maxSize = 50L * 1024L * 1024L
                )
            )
            .addNetworkInterceptor(headerInterceptor)
            .addNetworkInterceptor(ChuckerInterceptor(applicationContext))
            .build()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().apply {
            registerTypeAdapter(
                Date::class.java,
                DateTypeAdapter()
            )
        }.create()
    }

    @Singleton
    @Provides
    fun provideNetworkService(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): NetworkService {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(NetworkService::class.java)
    }
}