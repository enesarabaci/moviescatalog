package com.example.moviescatalog.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.moviescatalog.model.MovieCatalog
import com.example.moviescatalog.model.MovieData
import com.example.moviescatalog.network.network.NetworkService

class CatalogPagingSource(
    private val networkService: NetworkService,
    private val movieCatalog: MovieCatalog
) : PagingSource<Int, MovieData>() {

    companion object {
        const val STARTING_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, MovieData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieData> {
        val position = params.key ?: 1

        return try {
            val response = networkService.getMovies(
                sortBy = movieCatalog.sortByQuery,
                page = position
            )
            val data = response.toMovieListData().movies

            LoadResult.Page(
                data = data,
                prevKey = if (position == STARTING_INDEX) null else position - 1,
                nextKey = if (data.isEmpty()) null else position + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}