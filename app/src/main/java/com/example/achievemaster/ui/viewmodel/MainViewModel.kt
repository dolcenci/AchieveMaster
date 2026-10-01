package com.example.achievemaster.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.achievemaster.data.model.Exercise
import com.example.achievemaster.data.repository.ExerciseRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = ExerciseRepository()

    private val _exercises = MutableLiveData<List<Exercise>>(emptyList())
    val exercises: LiveData<List<Exercise>> = _exercises

    private val _filteredExercises = MutableLiveData<List<Exercise>>(emptyList())
    val filteredExercises: LiveData<List<Exercise>> = _filteredExercises

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchExercises() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val exerciseList = repository.getExercises()
                if (exerciseList.isNotEmpty()) {
                    _exercises.value = exerciseList
                    _filteredExercises.value = exerciseList
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "No exercises found. Please check your internet connection and API key."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}\nPlease check your internet connection."
                _exercises.value = emptyList()
                _filteredExercises.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        val allExercises = _exercises.value ?: return
        if (category == "All") {
            _filteredExercises.value = allExercises
        } else {
            _filteredExercises.value = allExercises.filter {
                it.category.equals(category, ignoreCase = true)
            }
        }
    }

    fun searchExercises(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.searchExercises(query)
                _filteredExercises.value = results
                if (results.isEmpty() && query.isNotEmpty()) {
                    _errorMessage.value = "No exercises found for '$query'"
                } else {
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}