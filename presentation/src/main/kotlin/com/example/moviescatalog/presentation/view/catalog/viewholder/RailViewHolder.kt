package com.example.moviescatalog.presentation.view.catalog.viewholder

import android.os.Parcelable
import androidx.core.view.isVisible
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.presentation.extension.getMessage
import com.example.moviescatalog.presentation.extension.getTitle
import com.example.moviescatalog.presentation.view.catalog.adapter.RailAdapter
import com.example.ui.databinding.ItemRailBinding

class RailViewHolder(
    private val binding: ItemRailBinding,
    itemDecoration: ItemDecoration,
    private val onMovieClickListener: (id: Int) -> Unit,
    private val onScrollStateChangedListener: ((Int, Parcelable?) -> Unit),
    private val submitData: ((adapter: PagingDataAdapter<MovieData, PosterViewHolder>, data: PagingData<MovieData>) -> Unit),
    private val handleLoadState: (adapter: PagingDataAdapter<MovieData, PosterViewHolder>, catalog: MovieCatalog) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val railAdapter by lazy {
        RailAdapter(onMovieClickListener)
    }

    fun bind(
        catalogState: CatalogState<PagingData<MovieData>>,
        savedState: Parcelable?
    ) {
        val context = binding.root.context

        savedState?.let { state ->
            binding.railRecyclerView.layoutManager?.onRestoreInstanceState(state)
        }

        binding.catalogTitleTextView.text = catalogState.catalog.getTitle(context)
        binding.railErrorTextView.isVisible = catalogState is CatalogState.Error
        binding.loadingProgressBar.isVisible = catalogState is CatalogState.Loading

        when (catalogState) {
            is CatalogState.Error -> {
                binding.railErrorTextView.text = catalogState.errorType.getMessage(context)
            }

            is CatalogState.Idle -> {}
            is CatalogState.Loading -> {}
            is CatalogState.Success -> {
                submitData(railAdapter, catalogState.data)
            }
        }

        if (railAdapter.snapshot().items.isEmpty()) {
            handleLoadState(railAdapter, catalogState.catalog)
        }
    }

    init {
        binding.railRecyclerView.adapter = railAdapter
        binding.railRecyclerView.addItemDecoration(itemDecoration)

        binding.railRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                onScrollStateChangedListener(
                    absoluteAdapterPosition,
                    recyclerView.layoutManager?.onSaveInstanceState()
                )
            }
        })
    }
}