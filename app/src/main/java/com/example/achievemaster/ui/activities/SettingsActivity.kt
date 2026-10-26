package com.example.achievemaster.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.achievemaster.R
import com.example.achievemaster.utils.LocaleHelper
import com.example.achievemaster.utils.SharedPrefsHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPrefsHelper
    private lateinit var switchNotifications: Switch
    private lateinit var switchDarkMode: Switch
    private lateinit var etFitnessGoal: EditText
    private lateinit var etWorkoutFrequency: EditText
    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private lateinit var layoutLanguage: LinearLayout
    private lateinit var layoutPrivacyPolicy: LinearLayout
    private lateinit var layoutShareApp: LinearLayout
    private lateinit var layoutAbout: LinearLayout
    private lateinit var tvLanguageValue: TextView

    companion object {
        const val KEY_DARK_MODE = "dark_mode"
        const val PREFS_NAME = "settings"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

        switchNotifications = findViewById(R.id.switchNotifications)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        etFitnessGoal = findViewById(R.id.etFitnessGoal)
        etWorkoutFrequency = findViewById(R.id.etWorkoutFrequency)
        btnSave = findViewById(R.id.btnSave)
        btnReset = findViewById(R.id.btnReset)
        layoutLanguage = findViewById(R.id.layoutLanguage)
        layoutPrivacyPolicy = findViewById(R.id.layoutPrivacyPolicy)
        layoutShareApp = findViewById(R.id.layoutShareApp)
        layoutAbout = findViewById(R.id.layoutAbout)
        tvLanguageValue = findViewById(R.id.tvLanguageValue)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        switchNotifications.isChecked = sharedPrefs.areNotificationsEnabled()
        switchDarkMode.isChecked = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
        etFitnessGoal.setText(sharedPrefs.getFitnessGoal())
        etWorkoutFrequency.setText(sharedPrefs.getWorkoutFrequency())
        tvLanguageValue.text = getLanguageDisplayName(LocaleHelper.getSavedLanguage(this))
    }

    private fun setupListeners() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.saveSetting("notifications_enabled", isChecked)
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DARK_MODE, isChecked)
                .apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        layoutLanguage.setOnClickListener { showLanguageDialog() }

        layoutPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        btnSave.setOnClickListener {
            sharedPrefs.saveSetting("notifications_enabled", switchNotifications.isChecked)
            sharedPrefs.saveSetting("fitness_goal", etFitnessGoal.text.toString())
            sharedPrefs.saveSetting("workout_frequency", etWorkoutFrequency.text.toString())
            Toast.makeText(this, getString(R.string.save) + " ✔", Toast.LENGTH_SHORT).show()
        }

        btnReset.setOnClickListener {
            switchNotifications.isChecked = true
            etFitnessGoal.setText("General Fitness")
            etWorkoutFrequency.setText("3 times per week")
            sharedPrefs.saveSetting("notifications_enabled", true)
            sharedPrefs.saveSetting("fitness_goal", "General Fitness")
            sharedPrefs.saveSetting("workout_frequency", "3 times per week")
            Toast.makeText(this, R.string.reset, Toast.LENGTH_SHORT).show()
        }

        layoutShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "AchieveMaster")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "I'm using AchieveMaster to track my fitness goals. Download it now!"
                )
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }

        layoutAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.about_achiever)
                .setMessage(
                    "AchieveMaster v1.0.0\n\n" +
                            "Your personal fitness companion. Track exercises, set goals, and achieve more every day.\n\n" +
                            "Built with Kotlin and the API Ninjas Exercises API.\n\n" +
                            "© 2024 AchieveMaster"
                )
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }

    private fun showLanguageDialog() {
        val codes = arrayOf("en", "zu", "xh", "af")
        val names = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_zulu),
            getString(R.string.language_xhosa),
            getString(R.string.language_afrikaans)
        )
        val currentCode = LocaleHelper.getSavedLanguage(this)
        val currentIndex = codes.indexOf(currentCode).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.language)
            .setSingleChoiceItems(names, currentIndex) { dialog, which ->
                val selectedCode = codes[which]
                LocaleHelper.saveLanguage(this, selectedCode)
                dialog.dismiss()
                recreate()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun getLanguageDisplayName(code: String): String = when (code) {
        "zu" -> getString(R.string.language_zulu)
        "xh" -> getString(R.string.language_xhosa)
        "af" -> getString(R.string.language_afrikaans)
        else -> getString(R.string.language_english)
    }
}