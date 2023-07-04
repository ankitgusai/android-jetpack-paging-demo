package com.ankit.nasaimages.networking

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ankit.nasaimages.networking.models.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Nasa image repo backed by a network source
 */
class NasaImageRepository(
    private val nasaAPI: NasaAPI,
    private val totalEntries: MutableStateFlow<Int>
) {

    /**
     * Search image pagination Flow.
     */
    fun searchImages(query: String): Flow<PagingData<Item>> {
        return Pager(PagingConfig(pageSize = 20)) {
            SearchImageSource(nasaAPI, query, totalEntries)
        }.flow
    }

    /**
     * Provides image assets based on nasa Id.
     */
    fun getImageAssetDetail(nasaId: String) = flow {
        val res = nasaAPI.getAssetById(nasaId)
        emit(res)
    }

    /**
     * Network backed search query resource.
     */
    class SearchImageSource(
        private val api: NasaAPI,
        private val query: String,
        private val totalEntries: MutableStateFlow<Int>
    ) : PagingSource<String, Item>() {
        override fun getRefreshKey(state: PagingState<String, Item>): String? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.nextKey ?: anchorPage?.prevKey
            }
        }

        override suspend fun load(params: LoadParams<String>): LoadResult<String, Item> {
            return try {
                //The key here is the url provided in the REST API response
                val response = if (params.key != null) {
                    api.search(params.key as String)
                } else {
                    api.search(query, 1, 20)
                }

                val nextKey = response.collection.links?.find { it.rel == "next" }?.href
                val prevKey = null // not providing ability to go back

                totalEntries.emit(response.collection.metadata.total_hits) // update the total entries

                LoadResult.Page(
                    data = response.collection.items,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } catch (e: Exception) {
                e.printStackTrace()
                LoadResult.Error(e)
            }
        }
    }
}