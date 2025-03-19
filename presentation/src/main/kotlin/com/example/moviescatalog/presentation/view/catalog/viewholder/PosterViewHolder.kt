package com.example.moviescatalog.presentation.view.catalog.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.presentation.extension.dpToPx
import com.example.moviescatalog.presentation.extension.enableCustomTouchEffect
import com.example.moviescatalog.presentation.extension.loadImage
import com.example.ui.databinding.ItemPosterBinding

class PosterViewHolder(
    private val binding: ItemPosterBinding,
    private val onMovieClickListener: (id: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.enableCustomTouchEffect()
    }

    fun bind(movie: MovieData) {
        binding.posterImageView.loadImage(
            url = movie.posterUrl ?: "",
            cornerRadius = binding.root.context.dpToPx(16)
        )

        binding.root.setOnClickListener {
            movie.id?.let(onMovieClickListener)
        }
    }
}