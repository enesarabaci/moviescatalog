package com.example.moviescatalog.presentation.extension

import android.content.Context
import com.example.moviescatalog.model.MovieCatalog
import com.example.ui.R

fun MovieCatalog.getTitle(context: Context): String {
    return when (this) {
        MovieCatalog.POPULAR -> context.getString(R.string.title_catalog_popular)
        MovieCatalog.TOP_RATED -> context.getString(R.string.title_catalog_top_rated)
        MovieCatalog.REVENUE -> context.getString(R.string.title_catalog_revenue)
        MovieCatalog.RELEASE_DATE -> context.getString(R.string.title_catalog_release_date)
    }
}