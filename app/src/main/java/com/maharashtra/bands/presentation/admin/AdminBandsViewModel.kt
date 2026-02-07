package com.maharashtra.bands.presentation.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.data.repository.FirestoreRepository


class AdminBandsViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {
    private val _bands = MutableLiveData<List<Band>>(emptyList())
    val bands: LiveData<List<Band>> = _bands

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private var listenerRegistration: ListenerRegistration? = null

    fun startListening() {
        listenerRegistration?.remove()
        listenerRegistration = repository.observeBands(
            onSuccess = { bands ->
                _bands.value = bands
            },
            onFailure = { exception ->
                _errorMessage.value = exception.message
            }
        )
    }

    fun deleteBand(bandId: String) {
        repository.deleteBand(
            bandId = bandId,
            onSuccess = {},
            onFailure = { exception ->
                _errorMessage.value = exception.message
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
