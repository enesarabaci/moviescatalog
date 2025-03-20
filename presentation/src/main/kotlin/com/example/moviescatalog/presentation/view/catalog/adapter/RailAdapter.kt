package com.example.moviescatalog.presentation.view.catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.presentation.view.catalog.viewholder.PosterViewHolder
import com.example.ui.databinding.ItemPosterBinding

class RailAdapter(
    private val onMovieClickListener: (id: Int) -> Unit
) : PagingDataAdapter<MovieData, PosterViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<MovieData>() {
            override fun areItemsTheSame(
                oldItem: MovieData,
                newItem: MovieData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MovieData,
                newItem: MovieData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val itemPosterBinding = ItemPosterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PosterViewHolder(itemPosterBinding, onMovieClickListener)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        getItem(position)?.let { movieData ->
            holder.bind(movieData)
        }
    }
}