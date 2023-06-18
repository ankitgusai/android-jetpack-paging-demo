package com.ankit.workdaytest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ankit.workdaytest.networking.NasaAPINetwork
import com.ankit.workdaytest.networking.NasaImageRepository
import com.ankit.workdaytest.networking.models.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Total entries StateFlow
    private val _totalEntries = MutableStateFlow(0)
    val totalEntries: StateFlow<Int>
        get() = _totalEntries

    private val repo: NasaImageRepository = NasaImageRepository(NasaAPINetwork.api, _totalEntries)

    // StateFlow for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    // Flow of PagingData
    val searchResults: Flow<PagingData<Item>> = _searchQuery.flatMapLatest { query ->
        repo.searchImages(query)
    }.cachedIn(viewModelScope)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

}

sealed class SearchImageUiState {
    object Loading : SearchImageUiState()
    class Failure(val msg: Throwable) : SearchImageUiState()
    class Success(val data: Pair<Int, List<Item>>) : SearchImageUiState()
    object Empty : SearchImageUiState()
}