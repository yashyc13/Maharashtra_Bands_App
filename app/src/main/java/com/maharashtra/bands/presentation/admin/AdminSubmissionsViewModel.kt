package com.maharashtra.bands.presentation.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.maharashtra.bands.data.model.Submission
import com.maharashtra.bands.data.repository.FirestoreRepository


class AdminSubmissionsViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {
    private val _submissions = MutableLiveData<List<Submission>>(emptyList())
    val submissions: LiveData<List<Submission>> = _submissions

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private var listenerRegistration: ListenerRegistration? = null

    fun startListening() {
        _isLoading.value = true
        listenerRegistration?.remove()
        listenerRegistration = repository.observePendingSubmissions(
            onSuccess = { submissions ->
                _submissions.value = submissions
                _isLoading.value = false
            },
            onFailure = { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
        )
    }

    fun approveSubmission(submission: Submission) {
        repository.approveSubmission(
            submission = submission,
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
