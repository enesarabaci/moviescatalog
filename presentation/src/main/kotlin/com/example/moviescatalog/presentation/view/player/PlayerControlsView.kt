package com.example.moviescatalog.presentation.view.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ui.databinding.ViewPlayerControlsBinding

class PlayerControlsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val viewBinding = ViewPlayerControlsBinding.inflate(
        LayoutInflater.from(context),
        this
    )
}