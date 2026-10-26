package com.example.achievemaster.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FitnessGoalTest {

    @Test
    fun progressPercent_whenNoTarget_returnsZero() {
        val goal = FitnessGoal(targetExercises = 0, completedExercises = 0)
        assertEquals(0, goal.progressPercent)
    }

    @Test
    fun progressPercent_whenOneThirdCompleted_returns33() {
        val goal = FitnessGoal(targetExercises = 3, completedExercises = 1)
        assertEquals(33, goal.progressPercent)
    }

    @Test
    fun progressPercent_whenFullyCompleted_returns100() {
        val goal = FitnessGoal(targetExercises = 3, completedExercises = 3)
        assertEquals(100, goal.progressPercent)
    }

    @Test
    fun progressPercent_whenOverCompleted_returnsMax100() {
        val goal = FitnessGoal(targetExercises = 3, completedExercises = 5)
        assertEquals(100, goal.progressPercent)
    }
}