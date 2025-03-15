package com.example.moviescatalog.presentation.view.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moviescatalog.model.MovieData
import com.example.ui.databinding.ItemPosterBinding

class RailAdapter : RecyclerView.Adapter<PosterViewHolder>() {

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

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    private val currentList: List<MovieData>
        get() = recyclerListDiffer.currentList

    fun updateList(newList: List<MovieData>) {
        recyclerListDiffer.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val itemPosterBinding = ItemPosterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PosterViewHolder(itemPosterBinding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        val movieData = currentList[position]
        holder.bind(movieData)
    }
}