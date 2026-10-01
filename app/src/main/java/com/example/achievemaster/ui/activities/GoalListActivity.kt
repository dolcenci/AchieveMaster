package com.example.achievemaster.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.achievemaster.R
import com.example.achievemaster.data.model.FitnessGoal
import com.example.achievemaster.ui.adapters.GoalAdapter
import com.example.achievemaster.utils.SharedPrefsHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GoalListActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPrefsHelper
    private lateinit var btnBack: ImageButton
    private lateinit var btnAddGoal: FloatingActionButton
    private lateinit var rvGoals: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var adapter: GoalAdapter

    private val goalsList = mutableListOf<FitnessGoal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_list)

        sharedPrefs = SharedPrefsHelper(this)

        btnBack = findViewById(R.id.btnBack)
        btnAddGoal = findViewById(R.id.btnAddGoal)
        rvGoals = findViewById(R.id.rvGoals)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        adapter = GoalAdapter(goalsList) { goal -> confirmDelete(goal) }
        rvGoals.layoutManager = LinearLayoutManager(this)
        rvGoals.adapter = adapter

        btnBack.setOnClickListener { finish() }

        btnAddGoal.setOnClickListener {
            startActivity(Intent(this, GoalCreationActivity::class.java))
            finish()
        }

        loadGoals()
    }

    private fun loadGoals() {
        goalsList.clear()
        goalsList.addAll(sharedPrefs.getAllGoals())
        adapter.updateData(goalsList)

        if (goalsList.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            rvGoals.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            rvGoals.visibility = View.VISIBLE
        }
    }

    private fun confirmDelete(goal: FitnessGoal) {
        AlertDialog.Builder(this)
            .setTitle("Delete Goal")
            .setMessage("Are you sure you want to delete \"${goal.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                sharedPrefs.deleteGoal(goal.id)
                loadGoals()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadGoals()
    }
}