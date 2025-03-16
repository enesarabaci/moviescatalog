package com.example.moviescatalog.presentation.extension

import android.content.Context
import com.example.moviescatalog.model.ErrorType
import com.example.ui.R

fun ErrorType.getMessage(context: Context): String {
    return when (this) {
        ErrorType.NETWORK -> context.getString(R.string.message_network_error)
        ErrorType.UNKNOWN -> context.getString(R.string.message_unknown_error)
    }
}