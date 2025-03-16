package com.example.moviescatalog.presentation.view.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieListData
import com.example.ui.databinding.ItemRailBinding

class CatalogAdapter(
    private val onMovieClickListener: (id: Int) -> Unit
) : RecyclerView.Adapter<RailViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<CatalogState<MovieListData>>() {
        override fun areItemsTheSame(
            oldItem: CatalogState<MovieListData>,
            newItem: CatalogState<MovieListData>
        ): Boolean {
            return oldItem.catalog == newItem.catalog
        }

        override fun areContentsTheSame(
            oldItem: CatalogState<MovieListData>,
            newItem: CatalogState<MovieListData>
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    private val currentList: List<CatalogState<MovieListData>>
        get() = recyclerListDiffer.currentList

    fun updateList(newList: List<CatalogState<MovieListData>>) {
        recyclerListDiffer.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RailViewHolder {
        val itemRailBinding = ItemRailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RailViewHolder(itemRailBinding, onMovieClickListener)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: RailViewHolder, position: Int) {
        val catalogState = currentList[position]
        holder.bind(catalogState)
    }
}