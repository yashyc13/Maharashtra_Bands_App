package com.maharashtra.bands.presentation.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class AdminLoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData(false)
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password required."
            return
        }
        if (_isLoading.value == true) return
        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _isLoading.value = false
                _loginSuccess.value = true
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message
            }
    }

    fun markLoginHandled() {
        _loginSuccess.value = false
    }
}
