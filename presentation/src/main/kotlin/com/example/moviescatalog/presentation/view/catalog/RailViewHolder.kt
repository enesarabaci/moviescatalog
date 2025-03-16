package com.example.moviescatalog.presentation.view.catalog

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieListData
import com.example.moviescatalog.presentation.extension.getMessage
import com.example.moviescatalog.presentation.extension.getTitle
import com.example.ui.databinding.ItemRailBinding

class RailViewHolder(
    private val binding: ItemRailBinding,
    itemDecoration: ItemDecoration,
    private val onMovieClickListener: (id: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val railAdapter by lazy {
        RailAdapter(onMovieClickListener)
    }

    fun bind(catalogState: CatalogState<MovieListData>) {
        val context = binding.root.context

        binding.catalogTitleTextView.text = catalogState.catalog.getTitle(context)
        binding.railErrorTextView.isVisible = catalogState is CatalogState.Error

        when (catalogState) {
            is CatalogState.Error -> {
                binding.railErrorTextView.text = catalogState.errorType.getMessage(context)
            }

            is CatalogState.Idle -> {}
            is CatalogState.Loading -> {}
            is CatalogState.Success -> {
                railAdapter.updateList(catalogState.data.movies)
            }
        }
    }

    init {
        binding.railRecyclerView.adapter = railAdapter
        binding.railRecyclerView.addItemDecoration(itemDecoration)
    }
}