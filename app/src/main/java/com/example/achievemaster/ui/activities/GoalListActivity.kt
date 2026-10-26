package com.example.achievemaster.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.achievemaster.R
import com.example.achievemaster.data.model.FitnessGoal
import com.example.achievemaster.data.network.FirebaseService
import com.example.achievemaster.ui.adapters.GoalAdapter
import com.example.achievemaster.utils.LocaleHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class GoalListActivity : AppCompatActivity() {

    private lateinit var firebaseService: FirebaseService
    private lateinit var btnBack: ImageButton
    private lateinit var btnAddGoal: FloatingActionButton
    private lateinit var btnAddGoalEmpty: Button
    private lateinit var rvGoals: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var adapter: GoalAdapter
    private val goalsList = mutableListOf<FitnessGoal>()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_list)

        firebaseService = FirebaseService()
        btnBack = findViewById(R.id.btnBack)
        btnAddGoal = findViewById(R.id.btnAddGoal)
        btnAddGoalEmpty = findViewById(R.id.btnAddGoalEmpty)
        rvGoals = findViewById(R.id.rvGoals)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        adapter = GoalAdapter(goalsList) { goal -> confirmDelete(goal) }
        rvGoals.layoutManager = LinearLayoutManager(this)
        rvGoals.adapter = adapter

        btnBack.setOnClickListener { finish() }

        val openCreate = {
            startActivity(Intent(this, GoalCreationActivity::class.java))
            finish()
        }
        btnAddGoal.setOnClickListener { openCreate() }
        btnAddGoalEmpty.setOnClickListener { openCreate() }

        loadGoals()
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val result = firebaseService.getAllGoals()
            if (result.isSuccess) {
                goalsList.clear()
                goalsList.addAll(result.getOrNull() ?: emptyList())
                adapter.updateData(goalsList)

                if (goalsList.isEmpty()) {
                    layoutEmpty.visibility = View.VISIBLE
                    rvGoals.visibility = View.GONE
                    btnAddGoal.visibility = View.GONE
                } else {
                    layoutEmpty.visibility = View.GONE
                    rvGoals.visibility = View.VISIBLE
                    btnAddGoal.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun confirmDelete(goal: FitnessGoal) {
        AlertDialog.Builder(this)
            .setTitle("Delete Goal")
            .setMessage("Delete \"${goal.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    firebaseService.deleteGoal(goal.id)
                    loadGoals()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadGoals()
    }
}