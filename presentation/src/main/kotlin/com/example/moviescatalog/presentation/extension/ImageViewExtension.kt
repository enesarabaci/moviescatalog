package com.example.moviescatalog.presentation.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(
    url: String,
    cornerRadius: Int? = null
) {
    val requestBuilder = Glide.with(this)
        .load(url)

    cornerRadius?.let { radius ->
        requestBuilder.apply(RequestOptions().transform(CenterCrop(), RoundedCorners(radius)))
    }

    requestBuilder.into(this)
}