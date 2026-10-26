package com.example.achievemaster.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.achievemaster.R
import com.example.achievemaster.data.model.FitnessGoal
import com.example.achievemaster.data.network.FirebaseService
import com.example.achievemaster.utils.LocaleHelper
import kotlinx.coroutines.launch
import java.util.UUID

class GoalCreationActivity : AppCompatActivity() {

    private lateinit var firebaseService: FirebaseService
    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveGoal: Button
    private lateinit var btnViewGoals: Button
    private lateinit var etGoalTitle: EditText
    private lateinit var etGoalDescription: EditText
    private lateinit var etTargetExercises: EditText
    private lateinit var spinnerMuscle: Spinner

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_creation)

        firebaseService = FirebaseService()
        btnBack = findViewById(R.id.btnBack)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        btnViewGoals = findViewById(R.id.btnViewGoals)
        etGoalTitle = findViewById(R.id.etGoalTitle)
        etGoalDescription = findViewById(R.id.etGoalDescription)
        etTargetExercises = findViewById(R.id.etTargetExercises)
        spinnerMuscle = findViewById(R.id.spinnerMuscle)

        val muscleGroups = listOf(
            "Abdominals", "Biceps", "Calves", "Chest",
            "Forearms", "Glutes", "Hamstrings", "Lats",
            "Lower Back", "Middle Back", "Neck", "Quadriceps",
            "Shoulders", "Traps", "Triceps"
        )
        spinnerMuscle.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            muscleGroups
        )

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
        val muscleDisplay = spinnerMuscle.selectedItem.toString()

        if (title.isEmpty() || targetStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val targetCount = targetStr.toIntOrNull() ?: 0
        if (targetCount <= 0) {
            Toast.makeText(this, "Enter a valid number greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        val apiMuscle = muscleDisplay
            .lowercase()
            .replace(" ", "_")

        val goal = FitnessGoal(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            targetExercises = targetCount,
            completedExercises = 0,
            relatedMuscle = apiMuscle,
            createdDate = System.currentTimeMillis(),
            targetDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
            isCompleted = false
        )

        lifecycleScope.launch {
            val result = firebaseService.saveGoal(goal)
            if (result.isSuccess) {
                Toast.makeText(
                    this@GoalCreationActivity,
                    "Goal created!",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@GoalCreationActivity, GoalListActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    this@GoalCreationActivity,
                    "Failed: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}