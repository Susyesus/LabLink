package com.lablink.android.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.data.api.RetrofitClient
import com.lablink.android.data.local.SessionManager
import com.lablink.android.data.model.ApiResponse
import com.lablink.android.data.model.ChangePasswordRequest
import com.lablink.android.databinding.ActivityChangePasswordBinding
import com.lablink.android.ui.auth.LoginActivity
import com.lablink.android.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnChangePassword.setOnClickListener { attemptChangePassword() }
    }

    private fun attemptChangePassword() {
        val currentPassword = binding.etCurrentPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // ── Validation ─────────────────────────────────────────────
        var hasError = false

        if (currentPassword.isEmpty()) {
            binding.tilCurrentPassword.error = "Current password is required"
            hasError = true
        } else {
            binding.tilCurrentPassword.error = null
        }

        if (newPassword.isEmpty()) {
            binding.tilNewPassword.error = "New password is required"
            hasError = true
        } else if (newPassword.length < 8) {
            binding.tilNewPassword.error = getString(R.string.error_password_length)
            hasError = true
        } else {
            binding.tilNewPassword.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your new password"
            hasError = true
        } else if (newPassword != confirmPassword) {
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

        val request = ChangePasswordRequest(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )

        RetrofitClient.instance.changePassword(request).enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(
                call: Call<ApiResponse<Void>>,
                response: Response<ApiResponse<Void>>
            ) {
                showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    binding.root.snackbarSuccess(getString(R.string.password_changed))

                    // Clear fields
                    binding.etCurrentPassword.text?.clear()
                    binding.etNewPassword.text?.clear()
                    binding.etConfirmPassword.text?.clear()

                    // Go back after delay
                    binding.root.postDelayed({ finish() }, 1500)
                } else if (response.code() == 401) {
                    handleUnauthorized()
                } else {
                    val errorMsg = parseErrorMessage(response)
                    binding.root.snackbarError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                showLoading(false)
                if (!NetworkUtils.isConnected(this@ChangePasswordActivity)) {
                    binding.root.snackbarError(getString(R.string.error_no_internet))
                } else {
                    binding.root.snackbarError(getString(R.string.error_network))
                }
            }
        })
    }

    private fun showLoading(loading: Boolean) {
        binding.btnChangePassword.isEnabled = !loading
        binding.btnChangePassword.text = if (loading) "" else getString(R.string.btn_change_password)
        if (loading) binding.progressChange.show() else binding.progressChange.hide()
    }

    private fun handleUnauthorized() {
        sessionManager.clearSession()
        toast(getString(R.string.error_session_expired))
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
