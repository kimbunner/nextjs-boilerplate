package com.example.shopeeclone

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shopeeclone.models.User
import com.example.shopeeclone.utils.FirebaseUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import android.widget.ProgressBar
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLoginPrompt: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tilFullName = findViewById(R.id.tilFullName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPhone = findViewById(R.id.tilPhone)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginPrompt = findViewById(R.id.tvLoginPrompt)
        progressBar = findViewById(R.id.progressBar)
        viewOverlay = findViewById(R.id.viewOverlay)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnRegister.setOnClickListener {
            if (validateInputs()) {
                performRegistration()
            }
        }

        tvLoginPrompt.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Reset errors
        tilFullName.error = null
        tilEmail.error = null
        tilPhone.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null

        // Validate full name
        val fullName = etFullName.text.toString().trim()
        if (fullName.isEmpty()) {
            tilFullName.error = getString(R.string.error_required)
            isValid = false
        }

        // Validate email
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        // Validate phone
        val phone = etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            tilPhone.error = getString(R.string.error_required)
            isValid = false
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.error = getString(R.string.error_invalid_phone)
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

        // Validate confirm password
        val confirmPassword = etConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = getString(R.string.error_required)
            isValid = false
        } else if (confirmPassword != password) {
            tilConfirmPassword.error = getString(R.string.error_passwords_not_match)
            isValid = false
        }

        return isValid
    }

    private fun performRegistration() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()

                when (val result = FirebaseUtils.safeCall { 
                    FirebaseUtils.signUp(email, password)
                }) {
                    is FirebaseUtils.FirebaseResult.Success -> {
                        val user = result.data.user
                        if (user != null) {
                            // Create user profile in Firestore
                            createUserProfile(user.uid)
                        } else {
                            showLoading(false)
                            Toast.makeText(
                                this@RegisterActivity,
                                getString(R.string.error_unknown),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    is FirebaseUtils.FirebaseResult.Error -> {
                        showLoading(false)
                        handleRegistrationError(result.exception)
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
                handleRegistrationError(e)
            }
        }
    }

    private suspend fun createUserProfile(userId: String) {
        val user = User(
            uid = userId,
            name = etFullName.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            phoneNumber = etPhone.text.toString().trim()
        )

        when (val result = FirebaseUtils.safeCall {
            FirebaseUtils.createUserProfile(user)
        }) {
            is FirebaseUtils.FirebaseResult.Success -> {
                Toast.makeText(
                    this,
                    getString(R.string.success_register),
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to Home screen
                startActivity(Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            is FirebaseUtils.FirebaseResult.Error -> {
                showLoading(false)
                handleRegistrationError(result.exception)
            }
        }
    }

    private fun handleRegistrationError(exception: Exception) {
        val errorMessage = when {
            exception.message?.contains("email already in use") == true ->
                "This email is already registered"
            exception.message?.contains("network") == true ->
                getString(R.string.error_network)
            else -> getString(R.string.error_unknown)
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        viewOverlay.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !show
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}
