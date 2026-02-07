package com.maharashtra.bands.presentation.submit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.maharashtra.bands.data.model.Submission
import com.maharashtra.bands.data.repository.FirestoreRepository


class SubmitBandViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {
    private val _isSubmitting = MutableLiveData(false)
    val isSubmitting: LiveData<Boolean> = _isSubmitting

    private val _submitSuccess = MutableLiveData(false)
    val submitSuccess: LiveData<Boolean> = _submitSuccess

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun submitBand(
        name: String,
        city: String,
        type: String,
        phoneNumber: String,
        imageUri: Uri?
    ) {
        if (_isSubmitting.value == true) return
        _isSubmitting.value = true
        _errorMessage.value = null

        if (imageUri == null) {
            submitBandData(name, city, type, phoneNumber, imageUrl = "")
            return
        }

        repository.uploadSubmissionImage(
            fileUri = imageUri,
            onSuccess = { url ->
                submitBandData(name, city, type, phoneNumber, imageUrl = url)
            },
            onFailure = { exception ->
                _isSubmitting.value = false
                _errorMessage.value = exception.message
            }
        )
    }

    private fun submitBandData(
        name: String,
        city: String,
        type: String,
        phoneNumber: String,
        imageUrl: String
    ) {
        val submission = Submission(
            bandName = name,
            city = city,
            type = type,
            phoneNumber = phoneNumber,
            imageUrl = imageUrl,
            status = STATUS_PENDING,
            createdAt = Timestamp.now()
        )

        repository.submitBand(
            submission = submission,
            onSuccess = {
                _isSubmitting.value = false
                _submitSuccess.value = true
            },
            onFailure = { exception ->
                _isSubmitting.value = false
                _errorMessage.value = exception.message
            }
        )
    }

    fun markSuccessHandled() {
        _submitSuccess.value = false
    }

    companion object {
        private const val STATUS_PENDING = "pending"
    }
}
