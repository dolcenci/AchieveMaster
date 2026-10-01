package com.example.achievemaster.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.achievemaster.R
import com.example.achievemaster.utils.SharedPrefsHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPrefsHelper
    private lateinit var switchNotifications: Switch
    private lateinit var switchDarkMode: Switch
    private lateinit var etFitnessGoal: EditText
    private lateinit var etWorkoutFrequency: EditText
    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private lateinit var layoutPrivacyPolicy: LinearLayout
    private lateinit var layoutShareApp: LinearLayout
    private lateinit var layoutAbout: LinearLayout

    companion object {
        const val KEY_DARK_MODE = "dark_mode"
        const val PREFS_NAME = "settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved dark-mode preference BEFORE setContentView
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val darkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPrefs = SharedPrefsHelper(this)

        // Wire up views
        switchNotifications = findViewById(R.id.switchNotifications)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        etFitnessGoal = findViewById(R.id.etFitnessGoal)
        etWorkoutFrequency = findViewById(R.id.etWorkoutFrequency)
        btnSave = findViewById(R.id.btnSave)
        btnReset = findViewById(R.id.btnReset)
        layoutPrivacyPolicy = findViewById(R.id.layoutPrivacyPolicy)
        layoutShareApp = findViewById(R.id.layoutShareApp)
        layoutAbout = findViewById(R.id.layoutAbout)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        switchNotifications.isChecked = sharedPrefs.areNotificationsEnabled()
        switchDarkMode.isChecked = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
        etFitnessGoal.setText(sharedPrefs.getFitnessGoal())
        etWorkoutFrequency.setText(sharedPrefs.getWorkoutFrequency())
    }

    private fun setupListeners() {
        // Notifications toggle saves immediately
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.saveSetting("notifications_enabled", isChecked)
        }

        // Dark mode toggle applies immediately
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DARK_MODE, isChecked)
                .apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Dark mode enabled", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "Light mode enabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Save button
        btnSave.setOnClickListener {
            sharedPrefs.saveSetting("notifications_enabled", switchNotifications.isChecked)
            sharedPrefs.saveSetting("fitness_goal", etFitnessGoal.text.toString())
            sharedPrefs.saveSetting("workout_frequency", etWorkoutFrequency.text.toString())
            Toast.makeText(this, "✅ Settings saved!", Toast.LENGTH_SHORT).show()
        }

        // Reset button
        btnReset.setOnClickListener {
            switchNotifications.isChecked = true
            etFitnessGoal.setText("General Fitness")
            etWorkoutFrequency.setText("3 times per week")
            sharedPrefs.saveSetting("notifications_enabled", true)
            sharedPrefs.saveSetting("fitness_goal", "General Fitness")
            sharedPrefs.saveSetting("workout_frequency", "3 times per week")
            Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
        }

        // Privacy Policy
        layoutPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        // Share App
        layoutShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out AchieveMaster!")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "I'm using AchieveMaster to track my fitness goals. Download it now!"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Share AchieveMaster"))
        }

        // About
        layoutAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("About AchieveMaster")
                .setMessage(
                    "AchieveMaster v1.0.0\n\n" +
                            "Your personal fitness companion. " +
                            "Track exercises, set goals, and achieve more every day.\n\n" +
                            "Built with Kotlin and the API Ninjas Exercises API.\n\n" +
                            "© 2024 AchieveMaster"
                )
                .setPositiveButton("OK", null)
                .show()
        }
    }
}