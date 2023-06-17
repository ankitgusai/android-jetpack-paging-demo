package com.ankit.workdaytest.networking

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ankit.workdaytest.networking.models.Items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class NasaImageRepository(
    private val nasaAPI: NasaAPI,
    private val totalEntries: MutableStateFlow<Int>
    ) {

    /**
     * Provides search image resource flow based on the query params for pagination.
     */
    fun searchImages(query: String): Flow<PagingData<Items>> {
        return Pager(PagingConfig(pageSize = 20)) {
            SearchImageSource(nasaAPI, query, totalEntries)
        }.flow
    }

    fun getImageAssetDetail(nasaId: String) = flow<Unit> {
        //todo implement the body
    }

    /**
     * Network backed search query resource.
     */
    class SearchImageSource(
        private val api: NasaAPI,
        private val query: String,
        private val totalEntries: MutableStateFlow<Int>) : PagingSource<String, Items>() {
        override fun getRefreshKey(state: PagingState<String, Items>): String?
    {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.nextKey ?: anchorPage?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Items> {
        return try {
            //The key here is the url provided in the REST API response
            val nextPage = params.key ?: api.searchInitialUrl(query, 1, 20)
            val response = api.search(nextPage)
            val nextKey = response.collection.links.find { it.rel == "next" }?.href
            val prevKey = null // not providing ability to go back

            totalEntries.emit(response.collection.metadata.total_hits) // update the total entries

            LoadResult.Page(
                data = response.collection.items,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            //TODO A decent error handling with appropriate message would be nice
            LoadResult.Error(e)
        }
    }

}
}