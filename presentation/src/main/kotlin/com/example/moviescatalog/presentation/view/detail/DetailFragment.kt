package com.example.moviescatalog.presentation.view.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.moviescatalog.model.DataState
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.presentation.extension.collectWhenStarted
import com.example.moviescatalog.presentation.extension.getMessage
import com.example.moviescatalog.presentation.extension.loadImage
import com.example.moviescatalog.presentation.view.player.PlayerActivity
import com.example.moviescatalog.presentation.viewmodel.DetailViewModel
import com.example.ui.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.movieDetailsStateFlow.collectWhenStarted(viewLifecycleOwner, ::updateUI)

        binding.watchButton.setOnClickListener {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            startActivity(playerIntent)
        }
    }

    private fun updateUI(state: DataState<MovieData>) {
        binding.loadingProgressBar.isVisible = state is DataState.Loading
        binding.watchButton.isVisible = state is DataState.Success
        binding.voteAverageTextView.isVisible = state is DataState.Success
        binding.releaseDateTextView.isVisible = state is DataState.Success
        binding.errorTextView.isVisible = state is DataState.Error

        when (state) {
            is DataState.Success<MovieData> -> {
                val movieData = state.data

                (movieData.backdropUrl ?: movieData.posterUrl)?.let {
                    binding.backdropImageView.loadImage(it)
                }

                binding.titleTextView.text = movieData.title
                binding.releaseDateTextView.text = movieData.releaseDate
                binding.voteAverageTextView.text = movieData.voteAverage?.toString()
                binding.overviewTextView.text = movieData.overview
                binding.errorTextView.text = ""
            }

            is DataState.Error -> {
                binding.errorTextView.text = state.errorType.getMessage(requireContext())
            }
            DataState.Loading -> {}
            DataState.Idle -> {}
        }
    }
}