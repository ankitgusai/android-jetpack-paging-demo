package com.ankit.nasaimages.ui

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ankit.nasaimages.networking.NasaAPINetwork
import com.ankit.nasaimages.networking.NasaImageRepository
import com.ankit.nasaimages.networking.models.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Total entries StateFlow
    private val _totalEntries = MutableStateFlow(0)
    //TODO set totalEntries in the header of Paging Adapter
    val totalEntries: StateFlow<Int>
        get() = _totalEntries

    private val repo: NasaImageRepository = NasaImageRepository(NasaAPINetwork.api, _totalEntries)

    // StateFlow for search query
    private val _searchQuery = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Flow of Paging Data
    val searchResults: Flow<PagingData<Item>> = _searchQuery
        .filter { !TextUtils.isEmpty(it) }
        .flatMapLatest { query -> repo.searchImages(query) }
        .cachedIn(viewModelScope)


    private val _selectedItem = MutableStateFlow<Item?>(null)
    val selectedItem: StateFlow<Item?> = _selectedItem

    fun selectItem(selectedItem: Item) {
        viewModelScope.launch {
            _selectedItem.emit(selectedItem)
        }
    }

    // Item detail flow, Could use StateFlow if we add Loading state in Response class
    val itemDetails: Flow<Response<String>>
        get() = _selectedItem.filter { searchedItem -> searchedItem != null }
            .flatMapLatest { searchedItem ->
                repo.getImageAssetDetail(searchedItem!!.data[0].nasa_id)
            }.map { assentRes ->
                val highestResJpegItem = assentRes.collection.items.find { it.href.endsWith("jpg") }

                if (highestResJpegItem != null) {
                    Response.Success(highestResJpegItem.href)

                } else {
                    Response.Error(IllegalStateException("Unsupported file format"))
                }

            }

}

sealed class Response<out T> {
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val exception: Throwable) : Response<Nothing>()
}
