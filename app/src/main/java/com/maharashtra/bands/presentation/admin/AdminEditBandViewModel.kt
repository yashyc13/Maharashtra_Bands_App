package com.maharashtra.bands.presentation.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.data.repository.FirestoreRepository


class AdminEditBandViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {
    private val _isSaving = MutableLiveData(false)
    val isSaving: LiveData<Boolean> = _isSaving

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    fun updateBand(band: Band) {
        if (_isSaving.value == true) return
        _isSaving.value = true
        _errorMessage.value = null

        repository.updateBand(
            band = band,
            onSuccess = {
                _isSaving.value = false
                _saveSuccess.value = true
            },
            onFailure = { exception ->
                _isSaving.value = false
                _errorMessage.value = exception.message
            }
        )
    }

    fun markSaveHandled() {
        _saveSuccess.value = false
    }
}
