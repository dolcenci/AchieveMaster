package com.example.achievemaster.ui.activities

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.achievemaster.R
import com.example.achievemaster.utils.LocaleHelper

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}