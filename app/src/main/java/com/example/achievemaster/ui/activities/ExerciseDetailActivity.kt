package com.example.achievemaster.ui.activities

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.achievemaster.R
import com.example.achievemaster.data.network.FirebaseService
import com.example.achievemaster.utils.LocaleHelper
import kotlinx.coroutines.launch

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var tvExerciseName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvMuscles: TextView
    private lateinit var tvEquipment: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var btnComplete: Button

    private lateinit var firebaseService: FirebaseService

    private var exerciseName: String = ""
    private var exerciseType: String = ""
    private var exerciseMuscle: String = ""
    private var exerciseDifficulty: String = ""

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)

        firebaseService = FirebaseService()

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
        btnComplete = findViewById(R.id.btnCompleteExercise)
    }

    private fun displayExercise() {
        try {
            exerciseName = intent.getStringExtra("exercise_name") ?: "Unknown Exercise"
            exerciseType = intent.getStringExtra("exercise_type") ?: "Unknown"
            exerciseMuscle = intent.getStringExtra("exercise_muscle") ?: "Unknown"
            exerciseDifficulty = intent.getStringExtra("exercise_difficulty") ?: "Unknown"
            val equipment = intent.getStringExtra("exercise_equipment") ?: "Bodyweight"
            val instructions = intent.getStringExtra("exercise_instructions")
                ?: "No instructions available"

            val category = when (exerciseType.lowercase()) {
                "strength" -> "Strength"
                "cardio" -> "Cardio"
                "stretching" -> "Flexibility"
                "powerlifting" -> "Strength"
                "olympic_weightlifting" -> "Strength"
                else -> exerciseType.replaceFirstChar { it.uppercase() }
            }

            tvExerciseName.text = exerciseName.replaceFirstChar { it.uppercase() }
            tvCategory.text = "${getString(R.string.category_prefix)} $category"
            tvDescription.text =
                "${getString(R.string.difficulty_prefix)} ${exerciseDifficulty.replaceFirstChar { it.uppercase() }}"
            tvMuscles.text =
                "${getString(R.string.muscle_prefix)} ${exerciseMuscle.replaceFirstChar { it.uppercase() }}"
            tvEquipment.text =
                "${getString(R.string.equipment_prefix)} ${equipment.ifEmpty { "Bodyweight" }}"
            tvInstructions.text = "${getString(R.string.instructions)}:\n$instructions"

            btnComplete.setOnClickListener { markAsCompleted() }

        } catch (e: Exception) {
            Toast.makeText(this, "${getString(R.string.error)}: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun markAsCompleted() {
        btnComplete.isEnabled = false

        firebaseService.logExerciseCompletion(
            exerciseName = exerciseName,
            muscle = exerciseMuscle,
            type = exerciseType,
            difficulty = exerciseDifficulty
        )

        firebaseService.incrementCompletedExercises(exerciseMuscle)

        Toast.makeText(
            this,
            "Logged!",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}