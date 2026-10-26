package com.example.achievemaster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.achievemaster.data.network.FirebaseService
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseService = FirebaseService()

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = firebaseService.loginUser(email, password)
                if (result.isSuccess) {
                    _loginResult.value = true
                } else {
                    _loginResult.value = false
                    _error.value = result.exceptionOrNull()?.message ?: "Login failed"
                }
            } catch (e: Exception) {
                _loginResult.value = false
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = firebaseService.registerUser(email, password, name)
                if (result.isSuccess) {
                    // IMPORTANT: Sign out immediately after registration
                    // so the user MUST log in with their new credentials
                    firebaseService.logout()
                    _loginResult.value = true
                } else {
                    _loginResult.value = false
                    _error.value = result.exceptionOrNull()?.message ?: "Registration failed"
                }
            } catch (e: Exception) {
                _loginResult.value = false
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        firebaseService.logout()
    }

    fun isLoggedIn(): Boolean {
        return firebaseService.isLoggedIn()
    }
}