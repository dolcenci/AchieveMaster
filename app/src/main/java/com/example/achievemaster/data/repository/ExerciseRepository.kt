package com.example.achievemaster.data.repository

import com.example.achievemaster.data.model.Exercise
import com.example.achievemaster.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ExerciseRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getExercises(): List<Exercise> {
        return withContext(Dispatchers.IO) {
            try {
                // Get exercises for different muscle groups
                val allExercises = mutableListOf<Exercise>()

                // Try to get exercises for common muscle groups
                val muscleGroups = listOf("chest", "back", "legs", "shoulders", "biceps", "triceps", "abs", "cardio")

                for (muscle in muscleGroups) {
                    try {
                        val exercises = apiService.getExercises(muscle = muscle)
                        allExercises.addAll(exercises)
                    } catch (e: Exception) {
                        // Continue to next muscle group if one fails
                        continue
                    }
                }

                // If we got no exercises, try without muscle filter
                if (allExercises.isEmpty()) {
                    try {
                        val exercises = apiService.getExercises()
                        allExercises.addAll(exercises)
                    } catch (e: Exception) {
                        // Return empty list
                    }
                }

                allExercises.distinctBy { it.name }
            } catch (e: IOException) {
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getExercisesByCategory(category: String): List<Exercise> {
        return withContext(Dispatchers.IO) {
            try {
                val allExercises = getExercises()
                allExercises.filter { it.category.equals(category, ignoreCase = true) }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun searchExercises(query: String): List<Exercise> {
        return withContext(Dispatchers.IO) {
            try {
                if (query.isEmpty()) {
                    getExercises()
                } else {
                    apiService.searchExercises(query)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}