package com.example.achievemaster.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.achievemaster.MainActivity
import com.example.achievemaster.R
import com.example.achievemaster.data.network.FirebaseService
import com.example.achievemaster.ui.viewmodel.LoginViewModel
import com.example.achievemaster.utils.LocaleHelper
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var firebaseService: FirebaseService
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvToggleMode: TextView
    private lateinit var tvNameLabel: TextView
    private lateinit var tvEmailLabel: TextView

    private var isLoginMode = true

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        firebaseService = FirebaseService()
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
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        progressBar = findViewById(R.id.progressBar)
        tvToggleMode = findViewById(R.id.tvToggleMode)
        tvNameLabel = findViewById(R.id.tvNameLabel)
        tvEmailLabel = findViewById(R.id.tvEmailLabel)
    }

    private fun setupUI() {
        setLoginMode(true)

        btnLogin.setOnClickListener {
            if (isLoginMode) performLogin() else performRegistration()
        }

        btnRegister.setOnClickListener {
            isLoginMode = !isLoginMode
            setLoginMode(isLoginMode)
        }

        tvToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            setLoginMode(isLoginMode)
        }

        btnGoogleSignIn.setOnClickListener { signInWithGoogle() }
    }

    private fun setLoginMode(isLogin: Boolean) {
        if (isLogin) {
            btnLogin.text = getString(R.string.login)
            btnRegister.text = getString(R.string.register)
            tvToggleMode.text = getString(R.string.no_account)
            etName.visibility = android.view.View.GONE
            etEmail.visibility = android.view.View.GONE
            tvNameLabel.visibility = android.view.View.GONE
            tvEmailLabel.visibility = android.view.View.GONE
        } else {
            btnLogin.text = getString(R.string.register)
            btnRegister.text = getString(R.string.login)
            tvToggleMode.text = getString(R.string.have_account)
            etName.visibility = android.view.View.VISIBLE
            etEmail.visibility = android.view.View.VISIBLE
            tvNameLabel.visibility = android.view.View.VISIBLE
            tvEmailLabel.visibility = android.view.View.VISIBLE
        }
    }

    private fun performLogin() {
        val email = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(email, password)
    }

    private fun performRegistration() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.register(name, email, password)
    }

    private fun signInWithGoogle() {
        progressBar.visibility = android.view.View.VISIBLE
        btnGoogleSignIn.isEnabled = false

        val credentialManager = CredentialManager.create(this)

        val webClientId = try {
            getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Toast.makeText(this, "Google Sign-In is not configured", Toast.LENGTH_LONG).show()
            progressBar.visibility = android.view.View.GONE
            btnGoogleSignIn.isEnabled = true
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                handleSignInResult(result)
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Google sign-in cancelled: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = android.view.View.GONE
                btnGoogleSignIn.isEnabled = true
            }
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                lifecycleScope.launch {
                    try {
                        val authResult = firebaseService.signInWithGoogleIdToken(idToken)

                        if (authResult.isSuccess) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Signed in with Google",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToMain()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Google sign-in failed: ${authResult.exceptionOrNull()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Firebase error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } finally {
                        progressBar.visibility = android.view.View.GONE
                        btnGoogleSignIn.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Could not parse Google credential: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = android.view.View.GONE
                btnGoogleSignIn.isEnabled = true
            }
        } else {
            Toast.makeText(
                this@LoginActivity,
                "Unexpected credential type: ${credential.type}",
                Toast.LENGTH_LONG
            ).show()
            progressBar.visibility = android.view.View.GONE
            btnGoogleSignIn.isEnabled = true
        }
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { success ->
            if (success == true) {
                if (isLoginMode) {
                    navigateToMain()
                } else {
                    val intent = Intent(this, SignupSuccessActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
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