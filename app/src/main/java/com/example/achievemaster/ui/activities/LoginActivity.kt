package com.example.achievemaster.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.achievemaster.MainActivity
import com.example.achievemaster.R
import com.example.achievemaster.ui.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvToggleMode: TextView
    private lateinit var tvNameLabel: TextView
    private lateinit var tvEmailLabel: TextView

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        setupUI()
        setupObservers()

        if (viewModel.isLoggedIn()) {
            navigateToMain()
        }
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
        tvToggleMode = findViewById(R.id.tvToggleMode)
        tvNameLabel = findViewById(R.id.tvNameLabel)
        tvEmailLabel = findViewById(R.id.tvEmailLabel)
    }

    private fun setupUI() {
        setLoginMode(true)

        btnLogin.setOnClickListener {
            if (isLoginMode) {
                performLogin()
            } else {
                performRegistration()
            }
        }

        btnRegister.setOnClickListener {
            isLoginMode = !isLoginMode
            setLoginMode(isLoginMode)
        }

        tvToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            setLoginMode(isLoginMode)
        }
    }

    private fun setLoginMode(isLogin: Boolean) {
        if (isLogin) {
            btnLogin.text = "Login"
            btnRegister.text = "Register"
            tvToggleMode.text = "Don't have an account? Register"
            etName.visibility = android.view.View.GONE
            etEmail.visibility = android.view.View.GONE
            tvNameLabel.visibility = android.view.View.GONE
            tvEmailLabel.visibility = android.view.View.GONE
        } else {
            btnLogin.text = "Register"
            btnRegister.text = "Back to Login"
            tvToggleMode.text = "Already have an account? Login"
            etName.visibility = android.view.View.VISIBLE
            etEmail.visibility = android.view.View.VISIBLE
            tvNameLabel.visibility = android.view.View.VISIBLE
            tvEmailLabel.visibility = android.view.View.VISIBLE
        }
    }

    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(username, password)
    }

    private fun performRegistration() {
        val name = etName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.register(name, username, email, password)
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { success ->
            if (success == true) {
                if (isLoginMode) {
                    navigateToMain()
                } else {
                    // Registration succeeded — show success screen
                    startActivity(Intent(this, SignupSuccessActivity::class.java))
                    finish()
                }
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            btnLogin.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}