package com.example.moviescatalog.presentation.view.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieListData
import com.example.moviescatalog.presentation.extension.collectWhenStarted
import com.example.moviescatalog.presentation.extension.navigatePush
import com.example.moviescatalog.presentation.viewmodel.CatalogViewModel
import com.example.ui.R
import com.example.ui.databinding.FragmentCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class CatalogFragment : Fragment() {

    private lateinit var binding: FragmentCatalogBinding

    private val viewModel: CatalogViewModel by viewModels()

    private val catalogAdapter by lazy {
        CatalogAdapter(::onMovieClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatalogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.catalogRecyclerView.adapter = catalogAdapter

        viewModel.catalogStateFlow.collectWhenStarted(viewLifecycleOwner, ::updateCatalog)
    }

    private fun updateCatalog(catalog: List<CatalogState<MovieListData>>) {
        catalogAdapter.updateList(catalog)
    }

    private fun onMovieClickListener(id: Int) {
        findNavController().navigatePush(
            R.id.action_catalogFragment_to_detailFragment,
        )
    }
}