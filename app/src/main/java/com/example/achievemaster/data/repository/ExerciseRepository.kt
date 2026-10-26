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
                val allExercises = mutableListOf<Exercise>()

                // Valid muscle names per API Ninjas documentation
                val muscleGroups = listOf(
                    "chest", "lats", "middle_back", "lower_back",
                    "quadriceps", "hamstrings", "calves", "glutes",
                    "shoulders", "biceps", "triceps", "forearms",
                    "abdominals", "traps", "neck"
                )

                for (muscle in muscleGroups) {
                    try {
                        val exercises = apiService.getExercises(muscle = muscle)
                        allExercises.addAll(exercises)
                    } catch (e: Exception) {
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