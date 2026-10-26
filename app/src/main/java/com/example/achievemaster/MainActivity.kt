package com.example.achievemaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.achievemaster.data.network.FirebaseService
import com.example.achievemaster.data.network.RetrofitClient
import com.example.achievemaster.ui.activities.GoalListActivity
import com.example.achievemaster.ui.activities.LoginActivity
import com.example.achievemaster.ui.activities.SettingsActivity
import com.example.achievemaster.ui.adapters.ExerciseAdapter
import com.example.achievemaster.ui.viewmodel.MainViewModel
import com.example.achievemaster.utils.LocaleHelper
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var firebaseService: FirebaseService
    private lateinit var exerciseAdapter: ExerciseAdapter

    private lateinit var tvWelcome: TextView
    private lateinit var tvUserInfo: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button
    private lateinit var btnMyGoals: Button
    private lateinit var btnFilterAll: Button
    private lateinit var btnFilterStrength: Button
    private lateinit var btnFilterCardio: Button
    private lateinit var btnFilterFlexibility: Button
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageButton

    companion object {
        @Volatile
        private var persistenceEnabled = false
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.initialize(this)

        if (!persistenceEnabled) {
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true)
                persistenceEnabled = true
            } catch (e: Exception) {
                // Already enabled in this process — safe to ignore
            }
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        firebaseService = FirebaseService()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        if (!firebaseService.isLoggedIn()) {
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
        btnMyGoals = findViewById(R.id.btnMyGoals)
        btnFilterAll = findViewById(R.id.btnFilterAll)
        btnFilterStrength = findViewById(R.id.btnFilterStrength)
        btnFilterCardio = findViewById(R.id.btnFilterCardio)
        btnFilterFlexibility = findViewById(R.id.btnFilterFlexibility)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(this, emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter
    }

    private fun setupUI() {
        val user = firebaseService.getCurrentUser()
        val name = user?.displayName ?: user?.email ?: "User"
        tvWelcome.text = "${getString(R.string.welcome_user)}, $name! 👋"
        tvUserInfo.text = user?.email ?: ""

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            firebaseService.logout()
            navigateToLogin()
        }

        btnMyGoals.setOnClickListener {
            startActivity(Intent(this, GoalListActivity::class.java))
        }

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
    }

    private fun updateFilterButtons(selected: View) {
        listOf(btnFilterAll, btnFilterStrength, btnFilterCardio, btnFilterFlexibility).forEach {
            it.isSelected = false
        }
        selected.isSelected = true
    }

    private fun setupSearch() {
        btnSearch.setOnClickListener {
            viewModel.searchExercises(etSearch.text.toString())
        }
        etSearch.setOnEditorActionListener { _, _, _ ->
            viewModel.searchExercises(etSearch.text.toString())
            true
        }
    }

    private fun setupObservers() {
        viewModel.filteredExercises.observe(this) { exercises ->
            exerciseAdapter.updateData(exercises)
            tvError.visibility = View.GONE
        }
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                tvError.text = it
                tvError.visibility = View.VISIBLE
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