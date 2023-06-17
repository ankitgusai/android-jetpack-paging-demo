package com.ankit.workdaytest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ankit.workdaytest.networking.NasaAPINetwork
import com.ankit.workdaytest.networking.NasaImageRepository
import com.ankit.workdaytest.networking.models.Items
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Total entries StateFlow
    private val _totalEntries = MutableStateFlow(0)
    val totalEntries: StateFlow<Int>
        get() = _totalEntries
    private val repo: NasaImageRepository = NasaImageRepository(NasaAPINetwork.api, _totalEntries)



}

sealed class SearchImageUiState {
    object Loading : SearchImageUiState()
    class Failure(val msg: Throwable) : SearchImageUiState()
    class Success(val data: Pair<Int, List<Items>>) : SearchImageUiState()
    object Empty : SearchImageUiState()
}