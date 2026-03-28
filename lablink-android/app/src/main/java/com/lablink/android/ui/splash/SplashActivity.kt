package com.lablink.android.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.data.api.RetrofitClient
import com.lablink.android.data.local.SessionManager
import com.lablink.android.databinding.ActivitySplashBinding
import com.lablink.android.ui.auth.LoginActivity
import com.lablink.android.ui.dashboard.DashboardActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        // Animate logo and text
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.ivLogo.startAnimation(fadeIn)
        binding.tvAppName.startAnimation(fadeIn)
        binding.tvTagline.startAnimation(fadeIn)

        // Navigate after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val destination = if (sessionManager.isLoggedIn()) {
                Intent(this, DashboardActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(destination)
            finish()
            overridePendingTransition(R.anim.fade_in, 0)
        }, 2000)
    }
}
