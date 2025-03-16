package com.example.moviescatalog.presentation.view.catalog

import androidx.recyclerview.widget.RecyclerView
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.presentation.extension.loadImage
import com.example.ui.databinding.ItemPosterBinding

class PosterViewHolder(
    private val binding: ItemPosterBinding,
    private val onMovieClickListener: (id: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: MovieData) {
        movie.porterUrl?.let {
            binding.posterImageView.loadImage(it)
        }

        binding.root.setOnClickListener {
            movie.id?.let(onMovieClickListener)
        }
    }
}