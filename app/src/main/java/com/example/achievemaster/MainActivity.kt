package com.example.achievemaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.achievemaster.ui.activities.LoginActivity
import com.example.achievemaster.ui.activities.SettingsActivity
import com.example.achievemaster.ui.adapters.ExerciseAdapter
import com.example.achievemaster.ui.viewmodel.MainViewModel
import com.example.achievemaster.ui.activities.GoalListActivity
import com.example.achievemaster.utils.SharedPrefsHelper

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPrefs: SharedPrefsHelper
    private lateinit var exerciseAdapter: ExerciseAdapter

    private lateinit var tvWelcome: TextView
    private lateinit var tvUserInfo: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button
    private lateinit var btnFilterAll: Button
    private lateinit var btnFilterStrength: Button
    private lateinit var btnFilterCardio: Button
    private lateinit var btnFilterFlexibility: Button
    private lateinit var etSearch: EditText

    private lateinit var btnMyGoals: Button
    private lateinit var btnSearch: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()

        sharedPrefs = SharedPrefsHelper(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        if (!sharedPrefs.isLoggedIn()) {
            navigateToLogin()
            return
        }

        setupRecyclerView()
        setupUI()
        setupObservers()
        setupSearch()

        viewModel.fetchExercises()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvUserInfo = findViewById(R.id.tvUserInfo)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        btnSettings = findViewById(R.id.btnSettings)
        btnLogout = findViewById(R.id.btnLogout)
        btnFilterAll = findViewById(R.id.btnFilterAll)
        btnFilterStrength = findViewById(R.id.btnFilterStrength)
        btnFilterCardio = findViewById(R.id.btnFilterCardio)
        btnFilterFlexibility = findViewById(R.id.btnFilterFlexibility)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnMyGoals = findViewById(R.id.btnMyGoals)
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(this, emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter
    }

    private fun setupUI() {
        val session = sharedPrefs.getUserSession()
        tvWelcome.text = "Welcome, ${session.username}! 👋"
        tvUserInfo.text = "Email: ${session.email}\nGoal: ${sharedPrefs.getFitnessGoal()}"

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            sharedPrefs.clearSession()
            navigateToLogin()
        }

        // Category filters
        btnFilterAll.setOnClickListener {
            viewModel.filterByCategory("All")
            updateFilterButtons(it)
        }

        btnFilterStrength.setOnClickListener {
            viewModel.filterByCategory("Strength")
            updateFilterButtons(it)
        }

        btnFilterCardio.setOnClickListener {
            viewModel.filterByCategory("Cardio")
            updateFilterButtons(it)
        }

        btnFilterFlexibility.setOnClickListener {
            viewModel.filterByCategory("Flexibility")
            updateFilterButtons(it)
        }

        btnMyGoals.setOnClickListener {
            startActivity(Intent(this, GoalListActivity::class.java))
        }

    }

    private fun updateFilterButtons(selected: View) {
        // Reset all buttons
        listOf(btnFilterAll, btnFilterStrength, btnFilterCardio, btnFilterFlexibility).forEach {
            it.isSelected = false
        }
        selected.isSelected = true
    }

    private fun setupSearch() {
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            viewModel.searchExercises(query)
        }

        etSearch.setOnEditorActionListener { _, _, _ ->
            val query = etSearch.text.toString()
            viewModel.searchExercises(query)
            true
        }
    }

    private fun setupObservers() {
        viewModel.filteredExercises.observe(this) { exercises ->
            exerciseAdapter.updateData(exercises)
            tvError.visibility = android.view.View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                tvError.text = it
                tvError.visibility = android.view.View.VISIBLE
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}







