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
import com.lablink.android.data.model.RegisterRequest
import com.lablink.android.databinding.ActivityRegisterBinding
import com.lablink.android.ui.dashboard.DashboardActivity
import com.lablink.android.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.tilFullName.startAnimation(slideUp)
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvLoginLink.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val studentId = binding.etStudentId.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // ── Client-side validation ─────────────────────────────────
        var hasError = false

        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            hasError = true
        } else {
            binding.tilFullName.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            hasError = true
        } else if (!email.matches(Regex("^[a-zA-Z]+\\.[a-zA-Z]+@cit\\.edu$"))) {
            binding.tilEmail.error = "Email must follow format: firstname.lastname@cit.edu"
            hasError = true
        } else {
            binding.tilEmail.error = null
        }

        if (studentId.isNotEmpty() && !studentId.matches(Regex("^\\d{2}-\\d{4}-\\d{3}$"))) {
            binding.tilStudentId.error = getString(R.string.error_invalid_student_id)
            hasError = true
        } else {
            binding.tilStudentId.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            hasError = true
        } else if (password.length < 8) {
            binding.tilPassword.error = getString(R.string.error_password_length)
            hasError = true
        } else {
            binding.tilPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            hasError = true
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.error_passwords_mismatch)
            hasError = true
        } else {
            binding.tilConfirmPassword.error = null
        }

        if (hasError) return

        if (!NetworkUtils.isConnected(this)) {
            binding.root.snackbarError(getString(R.string.error_no_internet))
            return
        }

        showLoading(true)

        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            idNumber = studentId.ifEmpty { null }
        )

        RetrofitClient.instance.register(request).enqueue(object : Callback<ApiResponse<AuthData>> {
            override fun onResponse(
                call: Call<ApiResponse<AuthData>>,
                response: Response<ApiResponse<AuthData>>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    sessionManager.saveSession(authData)
                    RetrofitClient.init(sessionManager)

                    startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                    finishAffinity()
                    overridePendingTransition(R.anim.fade_in, 0)
                } else {
                    val errorMsg = parseErrorMessage(response)
                    binding.root.snackbarError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ApiResponse<AuthData>>, t: Throwable) {
                showLoading(false)
                if (!NetworkUtils.isConnected(this@RegisterActivity)) {
                    binding.root.snackbarError(getString(R.string.error_no_internet))
                } else {
                    binding.root.snackbarError(getString(R.string.error_network))
                }
            }
        })
    }

    private fun showLoading(loading: Boolean) {
        binding.btnRegister.isEnabled = !loading
        binding.btnRegister.text = if (loading) "" else getString(R.string.btn_register)
        if (loading) binding.progressRegister.show() else binding.progressRegister.hide()
    }
}
