package com.example.moviescatalog.presentation.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ui.R

fun ImageView.loadImage(
    url: String,
    cornerRadius: Int? = null
) {
    val requestBuilder = Glide.with(this)
        .load(url)
        .placeholder(R.drawable.bg_image_placeholder)
        .error(R.drawable.bg_image_error)

    cornerRadius?.let { radius ->
        requestBuilder.apply(RequestOptions().transform(CenterCrop(), RoundedCorners(radius)))
    }

    requestBuilder.into(this)
}