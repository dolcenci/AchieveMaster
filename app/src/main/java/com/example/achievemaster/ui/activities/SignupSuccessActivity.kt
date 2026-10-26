package com.example.achievemaster.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.achievemaster.R
import com.example.achievemaster.utils.LocaleHelper

class SignupSuccessActivity : AppCompatActivity() {

    private lateinit var btnContinue: Button
    private lateinit var tvTitle: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var tvWhatsNext: TextView
    private lateinit var tvGoalBullet: TextView
    private lateinit var tvBrowseBullet: TextView
    private lateinit var tvTrackBullet: TextView

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_success)

        btnContinue = findViewById(R.id.btnContinue)
        tvTitle = findViewById(R.id.tvSuccessTitle)
        tvWelcome = findViewById(R.id.tvSuccessWelcome)
        tvWhatsNext = findViewById(R.id.tvWhatsNext)
        tvGoalBullet = findViewById(R.id.tvGoalBullet)
        tvBrowseBullet = findViewById(R.id.tvBrowseBullet)
        tvTrackBullet = findViewById(R.id.tvTrackBullet)

        tvTitle.text = getString(R.string.account_created)
        tvWelcome.text = getString(R.string.welcome_to_achievemaster)
        tvWhatsNext.text = getString(R.string.whats_next)
        tvGoalBullet.text = getString(R.string.set_fitness_goals)
        tvBrowseBullet.text = getString(R.string.browse_exercises_bullet)
        tvTrackBullet.text = getString(R.string.track_progress_bullet)
        btnContinue.text = getString(R.string.continue_to_login)

        btnContinue.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}