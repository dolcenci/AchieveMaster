package com.example.achievemaster.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.achievemaster.R
import com.example.achievemaster.data.model.FitnessGoal
import com.example.achievemaster.utils.SharedPrefsHelper
import java.util.UUID

class GoalCreationActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPrefsHelper
    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveGoal: Button
    private lateinit var btnViewGoals: Button
    private lateinit var etGoalTitle: EditText
    private lateinit var etGoalDescription: EditText
    private lateinit var etTargetExercises: EditText
    private lateinit var spinnerMuscle: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_creation)

        sharedPrefs = SharedPrefsHelper(this)

        btnBack = findViewById(R.id.btnBack)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        btnViewGoals = findViewById(R.id.btnViewGoals)
        etGoalTitle = findViewById(R.id.etGoalTitle)
        etGoalDescription = findViewById(R.id.etGoalDescription)
        etTargetExercises = findViewById(R.id.etTargetExercises)
        spinnerMuscle = findViewById(R.id.spinnerMuscle)

        // Setup muscle group spinner
        val muscleGroups = listOf(
            "Chest", "Back", "Legs", "Shoulders",
            "Biceps", "Triceps", "Abs", "Cardio"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            muscleGroups
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMuscle.adapter = adapter

        btnBack.setOnClickListener { onBackPressed() }

        btnSaveGoal.setOnClickListener { saveGoal() }

        btnViewGoals.setOnClickListener {
            startActivity(Intent(this, GoalListActivity::class.java))
            finish()
        }
    }

    private fun saveGoal() {
        val title = etGoalTitle.text.toString().trim()
        val description = etGoalDescription.text.toString().trim()
        val targetStr = etTargetExercises.text.toString().trim()
        val muscle = spinnerMuscle.selectedItem.toString()

        // Validation
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a goal title", Toast.LENGTH_SHORT).show()
            etGoalTitle.requestFocus()
            return
        }

        if (targetStr.isEmpty()) {
            Toast.makeText(this, "Please enter target exercises count", Toast.LENGTH_SHORT).show()
            etTargetExercises.requestFocus()
            return
        }

        val targetCount = targetStr.toIntOrNull()
        if (targetCount == null || targetCount <= 0) {
            Toast.makeText(this, "Please enter a valid number greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the goal
        val goal = FitnessGoal(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            targetExercises = targetCount,
            completedExercises = 0,
            relatedMuscle = muscle.lowercase(),
            createdDate = System.currentTimeMillis(),
            targetDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
            isCompleted = false
        )

        // Save
        sharedPrefs.saveGoal(goal)

        Toast.makeText(this, "✅ Goal created successfully!", Toast.LENGTH_SHORT).show()

        // Navigate to goal list
        startActivity(Intent(this, GoalListActivity::class.java))
        finish()
    }
}