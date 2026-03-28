package com.lablink.android.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.data.api.RetrofitClient
import com.lablink.android.data.local.SessionManager
import com.lablink.android.data.model.ApiResponse
import com.lablink.android.data.model.AuthData
import com.lablink.android.data.model.LoginRequest
import com.lablink.android.databinding.ActivityLoginBinding
import com.lablink.android.ui.dashboard.DashboardActivity
import com.lablink.android.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Animate form elements
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.tilEmail.startAnimation(slideUp)
        binding.tilPassword.startAnimation(slideUp)
        binding.btnLogin.startAnimation(slideUp)
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Client-side validation
        if (email.isEmpty() || password.isEmpty()) {
            binding.root.snackbarError(getString(R.string.error_fields_required))
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            return
        }
        binding.tilEmail.error = null

        // Check connectivity
        if (!NetworkUtils.isConnected(this)) {
            binding.root.snackbarError(getString(R.string.error_no_internet))
            return
        }

        showLoading(true)

        val request = LoginRequest(email, password)
        RetrofitClient.instance.login(request).enqueue(object : Callback<ApiResponse<AuthData>> {
            override fun onResponse(
                call: Call<ApiResponse<AuthData>>,
                response: Response<ApiResponse<AuthData>>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    sessionManager.saveSession(authData)

                    // Re-init retrofit with new token
                    RetrofitClient.init(sessionManager)

                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finishAffinity()
                    overridePendingTransition(R.anim.fade_in, 0)
                } else {
                    val errorMsg = parseErrorMessage(response)
                    binding.root.snackbarError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ApiResponse<AuthData>>, t: Throwable) {
                showLoading(false)
                if (!NetworkUtils.isConnected(this@LoginActivity)) {
                    binding.root.snackbarError(getString(R.string.error_no_internet))
                } else {
                    binding.root.snackbarError(getString(R.string.error_network))
                }
            }
        })
    }

    private fun showLoading(loading: Boolean) {
        binding.btnLogin.isEnabled = !loading
        binding.btnLogin.text = if (loading) "" else getString(R.string.btn_login)
        if (loading) binding.progressLogin.show() else binding.progressLogin.hide()
    }
}
