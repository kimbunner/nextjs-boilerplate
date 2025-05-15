package com.example.shopeeclone

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.shopeeclone.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    private lateinit var ivLogo: ImageView
    private lateinit var tvAppName: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvVersion: TextView

    private val SPLASH_DELAY = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        initViews()
        
        // Start animations
        startAnimations()

        // Handle navigation after splash delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, SPLASH_DELAY)
    }

    private fun initViews() {
        ivLogo = findViewById(R.id.ivLogo)
        tvAppName = findViewById(R.id.tvAppName)
        progressBar = findViewById(R.id.progressBar)
        tvVersion = findViewById(R.id.tvVersion)

        // Set version name
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            tvVersion.text = getString(R.string.version, pInfo.versionName)
        } catch (e: Exception) {
            tvVersion.visibility = View.GONE
        }
    }

    private fun startAnimations() {
        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)

        // Set animation duration
        fadeIn.duration = 1000
        slideUp.duration = 1000

        // Start animations
        ivLogo.startAnimation(fadeIn)
        tvAppName.startAnimation(slideUp)
    }

    private fun checkAuthAndNavigate() {
        val currentUser = FirebaseUtils.getCurrentUser()
        
        val intent = if (currentUser != null) {
            // User is signed in, go to Home
            Intent(this, HomeActivity::class.java)
        } else {
            // No user is signed in, go to Login
            Intent(this, LoginActivity::class.java)
        }

        // Clear back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        
        // Optional: Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Hide system UI for immersive splash screen
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
