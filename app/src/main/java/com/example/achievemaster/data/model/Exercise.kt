package com.example.achievemaster.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Exercise(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("muscle")
    val muscle: String = "",
    @SerializedName("equipments")
    val equipments: List<String>? = null,
    @SerializedName("difficulty")
    val difficulty: String = "",
    @SerializedName("instructions")
    val instructions: String = ""
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

    val equipment: String
        get() = if (equipments.isNullOrEmpty()) "Bodyweight" else equipments.joinToString(", ")

    val equipmentList: List<String>
        get() = if (equipments.isNullOrEmpty()) listOf("Bodyweight") else equipments
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