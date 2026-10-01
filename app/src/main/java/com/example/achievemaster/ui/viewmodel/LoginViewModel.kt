package com.example.achievemaster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.achievemaster.data.model.UserSession
import com.example.achievemaster.data.repository.ExerciseRepository
import com.example.achievemaster.utils.EncryptionHelper
import com.example.achievemaster.utils.SharedPrefsHelper
import kotlinx.coroutines.launch
import javax.crypto.SecretKey

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExerciseRepository()
    private val sharedPrefs = SharedPrefsHelper(application)
    private val secretKey: SecretKey by lazy { EncryptionHelper.generateKey() }

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Since we're using a mock login for demo purposes
                // In a real app, you would validate against a backend
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    val encryptedPassword = EncryptionHelper.encryptPassword(password, secretKey)

                    val session = UserSession(
                        userId = 1,
                        username = username,
                        email = "$username@example.com",
                        isLoggedIn = true
                    )
                    sharedPrefs.saveUserSession(session)

                    _loginResult.value = true
                } else {
                    _loginResult.value = false
                    _error.value = "Invalid username or password"
                }
            } catch (e: Exception) {
                _loginResult.value = false
                _error.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val encryptedPassword = EncryptionHelper.encryptPassword(password, secretKey)

                val session = UserSession(
                    userId = System.currentTimeMillis().toInt(),
                    username = username,
                    email = email,
                    isLoggedIn = true
                )
                sharedPrefs.saveUserSession(session)

                _loginResult.value = true
            } catch (e: Exception) {
                _loginResult.value = false
                _error.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        sharedPrefs.clearSession()
    }

    fun isLoggedIn(): Boolean {
        return sharedPrefs.isLoggedIn()
    }
}