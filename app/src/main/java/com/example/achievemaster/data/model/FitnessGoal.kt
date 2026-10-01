package com.example.achievemaster.data.model

import java.io.Serializable

data class FitnessGoal(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val targetExercises: Int = 0,
    val completedExercises: Int = 0,
    val relatedMuscle: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val targetDate: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
) : Serializable {

    val progressPercent: Int
        get() = if (targetExercises == 0) 0
        else ((completedExercises.toFloat() / targetExercises) * 100).toInt().coerceIn(0, 100)
}