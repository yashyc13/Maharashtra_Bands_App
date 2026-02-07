package com.maharashtra.bands.presentation.bands

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.data.repository.FirestoreRepository
import com.maharashtra.bands.data.repository.Pagination


class BandListViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {
    private val _bands = MutableLiveData<List<Band>>(emptyList())
    val bands: LiveData<List<Band>> = _bands

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private var lastDocument: DocumentSnapshot? = null
    private var isLastPage = false
    private var isRequestInFlight = false

    private val pageSize = 20L

    fun loadInitial() {
        if (isRequestInFlight) return
        _bands.value = emptyList()
        lastDocument = null
        isLastPage = false
        fetchNextPage()
    }

    fun fetchNextPage() {
        if (isRequestInFlight || isLastPage) return
        isRequestInFlight = true
        _isLoading.value = true
        _errorMessage.value = null

        repository.getApprovedBands(
            pagination = Pagination(limit = pageSize, lastDocument = lastDocument),
            onSuccess = { newBands, newLastDocument ->
                val current = _bands.value.orEmpty()
                val updated = current + newBands
                _bands.value = updated
                _isEmpty.value = updated.isEmpty()
                lastDocument = newLastDocument
                if (newBands.size < pageSize) {
                    isLastPage = true
                }
                _isLoading.value = false
                isRequestInFlight = false
            },
            onFailure = { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
                _isEmpty.value = _bands.value.isNullOrEmpty()
                isRequestInFlight = false
            }
        )
    }
}
