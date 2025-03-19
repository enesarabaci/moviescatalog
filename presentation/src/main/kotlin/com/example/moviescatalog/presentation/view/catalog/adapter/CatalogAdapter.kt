package com.example.moviescatalog.presentation.view.catalog.adapter

import android.graphics.Rect
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieListData
import com.example.moviescatalog.presentation.extension.dpToPx
import com.example.moviescatalog.presentation.view.catalog.viewholder.RailViewHolder
import com.example.ui.databinding.ItemRailBinding

class CatalogAdapter(
    private val onMovieClickListener: (id: Int) -> Unit,
    private val onScrollStateChangedListener: ((Int, Parcelable?) -> Unit)
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

    private var innerRecyclerViewSavedStates: Map<Int, Parcelable?>? = null

    fun setInnerRecyclerViewSavedStates(savedStates: Map<Int, Parcelable?>) {
        innerRecyclerViewSavedStates = savedStates
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RailViewHolder {
        val itemRailBinding = ItemRailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RailViewHolder(
            itemRailBinding,
            itemDecoration,
            onMovieClickListener,
            onScrollStateChangedListener
        )
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: RailViewHolder, position: Int) {
        val catalogState = currentList[position]
        val savedState = innerRecyclerViewSavedStates?.get(position)

        holder.bind(
            catalogState = catalogState,
            savedState = savedState
        )
    }
}