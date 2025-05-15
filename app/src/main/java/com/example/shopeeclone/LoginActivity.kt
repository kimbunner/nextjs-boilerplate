package com.example.shopeeclone

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shopeeclone.utils.FirebaseUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import android.widget.ProgressBar
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegisterPrompt: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegisterPrompt = findViewById(R.id.tvRegisterPrompt)
        progressBar = findViewById(R.id.progressBar)
        viewOverlay = findViewById(R.id.viewOverlay)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        tvForgotPassword.setOnClickListener {
            // TODO: Implement forgot password functionality
            Toast.makeText(this, "Forgot password coming soon!", Toast.LENGTH_SHORT).show()
        }

        tvRegisterPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Reset errors
        tilEmail.error = null
        tilPassword.error = null

        // Validate email
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        // Validate password
        val password = etPassword.text.toString()
        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_required)
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = getString(R.string.error_password_short)
            isValid = false
        }

        return isValid
    }

    private fun performLogin() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()

                when (val result = FirebaseUtils.safeCall { 
                    FirebaseUtils.signIn(email, password)
                }) {
                    is FirebaseUtils.FirebaseResult.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.success_login),
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to Home screen
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    }
                    is FirebaseUtils.FirebaseResult.Error -> {
                        showLoading(false)
                        handleLoginError(result.exception)
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
                handleLoginError(e)
            }
        }
    }

    private fun handleLoginError(exception: Exception) {
        val errorMessage = when {
            exception.message?.contains("password") == true -> 
                getString(R.string.error_invalid_password)
            exception.message?.contains("no user record") == true -> 
                getString(R.string.error_user_not_found)
            exception.message?.contains("network") == true -> 
                getString(R.string.error_network)
            else -> getString(R.string.error_unknown)
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        viewOverlay.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
