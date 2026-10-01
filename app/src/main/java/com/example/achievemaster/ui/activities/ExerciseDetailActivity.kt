package com.example.achievemaster.ui.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.achievemaster.R
import com.example.achievemaster.data.model.Exercise

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var tvExerciseName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvMuscles: TextView
    private lateinit var tvEquipment: TextView
    private lateinit var tvInstructions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Exercise Details"

        initViews()
        displayExercise()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initViews() {
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvCategory = findViewById(R.id.tvCategory)
        tvDescription = findViewById(R.id.tvDescription)
        tvMuscles = findViewById(R.id.tvMuscles)
        tvEquipment = findViewById(R.id.tvEquipment)
        tvInstructions = findViewById(R.id.tvInstructions)
    }




    private fun displayExercise() {
        try {
            // Get data from intent extras
            val name = intent.getStringExtra("exercise_name") ?: "Unknown Exercise"
            val type = intent.getStringExtra("exercise_type") ?: "Unknown"
            val muscle = intent.getStringExtra("exercise_muscle") ?: "Unknown"
            val equipment = intent.getStringExtra("exercise_equipment") ?: "Bodyweight"
            val difficulty = intent.getStringExtra("exercise_difficulty") ?: "Unknown"
            val instructions = intent.getStringExtra("exercise_instructions") ?: "No instructions available"

            // Map type to category
            val category = when (type.lowercase()) {
                "strength" -> "Strength"
                "cardio" -> "Cardio"
                "stretching" -> "Flexibility"
                "powerlifting" -> "Strength"
                "olympic_weightlifting" -> "Strength"
                else -> type.replaceFirstChar { it.uppercase() }
            }

            // Display the data
            tvExerciseName.text = name.replaceFirstChar { it.uppercase() }
            tvCategory.text = "Category: $category"
            tvDescription.text = "Difficulty: ${difficulty.replaceFirstChar { it.uppercase() }}"
            tvMuscles.text = "Muscle: ${muscle.replaceFirstChar { it.uppercase() }}"
            tvEquipment.text = "Equipment: ${equipment.ifEmpty { "Bodyweight" }}"
            tvInstructions.text = "Instructions:\n$instructions"

        } catch (e: Exception) {
            Toast.makeText(this, "Error loading exercise: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}