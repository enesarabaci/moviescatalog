package com.example.moviescatalog.presentation.view.catalog

import android.graphics.Rect
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieListData
import com.example.moviescatalog.presentation.extension.dpToPx
import com.example.moviescatalog.presentation.extension.getMessage
import com.example.moviescatalog.presentation.extension.getTitle
import com.example.ui.databinding.ItemRailBinding

class RailViewHolder(
    private val binding: ItemRailBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val railAdapter by lazy {
        RailAdapter()
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

    private val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val context = view.context

            val position = parent.getChildLayoutPosition(view)

            val left = if (position == 0)
                context.dpToPx(16)
            else
                context.dpToPx(8)

            val right = if (position == (parent.adapter?.itemCount ?: 0) - 1)
                context.dpToPx(16)
            else
                0

            outRect.left = left
            outRect.right = right
        }
    }

    init {
        binding.railRecyclerView.adapter = railAdapter
        binding.railRecyclerView.addItemDecoration(itemDecoration)
    }
}