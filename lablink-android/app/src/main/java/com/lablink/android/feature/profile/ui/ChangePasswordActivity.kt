package com.lablink.android.feature.profile.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.network.RetrofitClient
import com.lablink.android.core.util.*
import com.lablink.android.databinding.ActivityChangePasswordBinding
import com.lablink.android.feature.auth.ui.LoginActivity
import com.lablink.android.feature.profile.contract.ChangePasswordContract
import com.lablink.android.feature.profile.data.ProfileRepository
import com.lablink.android.feature.profile.presenter.ChangePasswordPresenter

/**
 * Change Password screen — thin MVP View implementation.
 * All business logic is delegated to [ChangePasswordPresenter].
 */
class ChangePasswordActivity : AppCompatActivity(), ChangePasswordContract.View {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var presenter: ChangePasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        val repository = ProfileRepository(RetrofitClient.instance, sessionManager)
        presenter = ChangePasswordPresenter(repository, this)
        presenter.attachView(this)

        setupListeners()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnChangePassword.setOnClickListener {
            presenter.changePassword(
                currentPassword = binding.etCurrentPassword.text.toString().trim(),
                newPassword = binding.etNewPassword.text.toString().trim(),
                confirmPassword = binding.etConfirmPassword.text.toString().trim()
            )
        }
    }

    // ─── ChangePasswordContract.View Implementation ────────────

    override fun showLoading(show: Boolean) {
        binding.btnChangePassword.isEnabled = !show
        binding.btnChangePassword.text = if (show) "" else getString(R.string.btn_change_password)
        if (show) binding.progressChange.show() else binding.progressChange.hide()
    }

    override fun showError(message: String) {
        binding.root.snackbarError(message)
    }

    override fun showNetworkError() {
        binding.root.snackbarError(getString(R.string.error_no_internet))
    }

    override fun handleUnauthorized() {
        sessionManager.clearSession()
        toast(getString(R.string.error_session_expired))
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    override fun onPasswordChanged() {
        binding.root.snackbarSuccess(getString(R.string.password_changed))
        binding.root.postDelayed({ finish() }, 1500)
    }

    override fun clearFields() {
        binding.etCurrentPassword.text?.clear()
        binding.etNewPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
    }

    override fun setCurrentPasswordError(message: String?) {
        binding.tilCurrentPassword.error = message
    }

    override fun setNewPasswordError(message: String?) {
        binding.tilNewPassword.error = message
    }

    override fun setConfirmPasswordError(message: String?) {
        binding.tilConfirmPassword.error = message
    }
}
