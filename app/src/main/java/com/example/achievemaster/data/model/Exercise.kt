package com.example.achievemaster.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Exercise(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("muscle")
    val muscle: String,
    @SerializedName("equipment")
    val equipment: String,
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("instructions")
    val instructions: String
) : Serializable {
    // Map to match our UI expectations
    val category: String
        get() = when (type.lowercase()) {
            "strength" -> "Strength"
            "cardio" -> "Cardio"
            "stretching" -> "Flexibility"
            "powerlifting" -> "Strength"
            "olympic_weightlifting" -> "Strength"
            else -> type.replaceFirstChar { it.uppercase() }
        }

    val muscles: List<String>
        get() = listOf(muscle)

    val equipmentList: List<String>
        get() = if (equipment.isNotEmpty()) listOf(equipment) else listOf("Bodyweight")
}

data class ExerciseCategory(
    val name: String,
    val exercises: List<Exercise>
) : Serializable

data class UserSession(
    val userId: Int,
    val username: String,
    val email: String,
    val isLoggedIn: Boolean = false
) : Serializable

data class WorkoutLog(
    val exerciseId: Int,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Double,
    val date: String
) : Serializable