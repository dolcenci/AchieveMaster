package com.example.achievemaster.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseTest {

    @Test
    fun equipment_whenEquipmentsIsNull_returnsBodyweight() {
        val exercise = Exercise(equipments = null)
        assertEquals("Bodyweight", exercise.equipment)
    }

    @Test
    fun equipment_whenEquipmentsIsEmpty_returnsBodyweight() {
        val exercise = Exercise(equipments = emptyList())
        assertEquals("Bodyweight", exercise.equipment)
    }

    @Test
    fun equipment_whenEquipmentsHasOneItem_returnsThatItem() {
        val exercise = Exercise(equipments = listOf("dumbbell"))
        assertEquals("dumbbell", exercise.equipment)
    }

    @Test
    fun equipment_whenEquipmentsHasMultipleItems_returnsCommaSeparated() {
        val exercise = Exercise(equipments = listOf("dumbbell", "flat bench"))
        assertEquals("dumbbell, flat bench", exercise.equipment)
    }

    @Test
    fun category_whenTypeStrength_returnsStrength() {
        val exercise = Exercise(type = "strength")
        assertEquals("Strength", exercise.category)
    }

    @Test
    fun category_whenTypeCardio_returnsCardio() {
        val exercise = Exercise(type = "cardio")
        assertEquals("Cardio", exercise.category)
    }
}