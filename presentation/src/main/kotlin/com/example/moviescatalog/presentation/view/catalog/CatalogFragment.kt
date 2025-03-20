package com.example.moviescatalog.presentation.view.catalog

import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.presentation.extension.collectWhenStarted
import com.example.moviescatalog.presentation.extension.dpToPx
import com.example.moviescatalog.presentation.extension.fadeIn
import com.example.moviescatalog.presentation.extension.isTablet
import com.example.moviescatalog.presentation.extension.navigatePush
import com.example.moviescatalog.presentation.view.catalog.adapter.CatalogAdapter
import com.example.moviescatalog.presentation.view.catalog.viewholder.PosterViewHolder
import com.example.moviescatalog.presentation.view.detail.DetailFragment
import com.example.moviescatalog.presentation.viewmodel.CatalogViewModel
import com.example.ui.R
import com.example.ui.databinding.FragmentCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class CatalogFragment : Fragment() {

    private var binding: FragmentCatalogBinding? = null

    private val viewModel: CatalogViewModel by viewModels()

    private val catalogAdapter by lazy {
        CatalogAdapter(
            onMovieClickListener = ::onMovieClickListener,
            onScrollStateChangedListener = ::onScrollStateChangedListener,
            submitData = ::submitData,
            handleLoadState = ::handleLoadState
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FragmentCatalogBinding.inflate(inflater, container, false).also {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.catalogStateFlow.collectWhenStarted(viewLifecycleOwner, ::updateCatalog)

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel.fetchCatalogs()
        }
    }

    private fun updateCatalog(catalog: List<CatalogState<PagingData<MovieData>>>) {
        binding?.swipeRefreshLayout?.isRefreshing = false
        catalogAdapter.updateList(catalog)
    }

    private fun onMovieClickListener(id: Int) {
        if (requireContext().isTablet()) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.detailFragmentContainer, DetailFragment().also {
                    it.arguments = Bundle().apply {
                        putInt("id", id)
                    }
                })
                .commit()

            binding?.detailFragmentContainer?.alpha = 0f
            binding?.detailFragmentContainer?.fadeIn()
        } else {
            findNavController().navigatePush(
                CatalogFragmentDirections.actionCatalogFragmentToDetailFragment(id)
            )
        }
    }

    private fun setupRecyclerView() {
        catalogAdapter.setInnerRecyclerViewSavedStates(
            viewModel.getInnerRecyclerViewSavedStates()
        )

        binding?.catalogRecyclerView?.addItemDecoration(itemDecoration)
        binding?.catalogRecyclerView?.adapter = catalogAdapter
    }

    private fun onScrollStateChangedListener(position: Int, savedState: Parcelable?) {
        viewModel.updateInnerRecyclerViewSavedState(
            position = position,
            savedState = savedState
        )
    }

    private fun submitData(
        adapter: PagingDataAdapter<MovieData, PosterViewHolder>,
        data: PagingData<MovieData>
    ) {
        lifecycleScope.launch {
            adapter.submitData(data)
        }
    }

    private fun handleLoadState(
        adapter: PagingDataAdapter<MovieData, PosterViewHolder>,
        catalog: MovieCatalog
    ) {
        adapter.loadStateFlow.collectWhenStarted(viewLifecycleOwner) { loadStates ->
            if (adapter.itemCount > 0)
                return@collectWhenStarted

            val loadStateError = loadStates.refresh as? LoadState.Error
                ?: loadStates.append as? LoadState.Error
                ?: loadStates.prepend as? LoadState.Error

            loadStateError?.let {
                viewModel.showNetworkErrorForCatalog(catalog)
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

            val top = if (position == 0)
                context.dpToPx(48)
            else
                context.dpToPx(0)

            outRect.top = top
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.catalogRecyclerView?.adapter = null
        binding = null
    }
}