package com.example.moviescatalog.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.moviescatalog.model.CatalogState
import com.example.moviescatalog.model.MovieListData
import com.example.moviescatalog.presentation.extension.collectWhenStarted
import com.example.moviescatalog.presentation.viewmodel.CatalogViewModel
import com.example.ui.databinding.FragmentCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class CatalogFragment : Fragment() {

    private lateinit var binding: FragmentCatalogBinding

    private val viewModel: CatalogViewModel by viewModels()

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

        viewModel.catalogStateFlow.collectWhenStarted(viewLifecycleOwner, ::updateCatalog)
        viewModel.getMovies()
    }

    private fun updateCatalog(catalog: List<CatalogState<MovieListData>>) {

    }
}