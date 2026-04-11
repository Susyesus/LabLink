package com.lablink.android.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.network.RetrofitClient
import com.lablink.android.core.util.*
import com.lablink.android.databinding.ActivityRegisterBinding
import com.lablink.android.feature.auth.contract.RegisterContract
import com.lablink.android.feature.auth.data.AuthRepository
import com.lablink.android.feature.auth.presenter.RegisterPresenter
import com.lablink.android.feature.equipment.ui.DashboardActivity

/**
 * Register screen — thin MVP View implementation.
 * All business logic is delegated to [RegisterPresenter].
 */
class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var presenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        val repository = AuthRepository(RetrofitClient.instance, sessionManager)
        presenter = RegisterPresenter(repository, this)
        presenter.attachView(this)

        setupUI()
        setupListeners()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun setupUI() {
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.tilFullName.startAnimation(slideUp)
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            presenter.register(
                fullName = binding.etFullName.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                studentId = binding.etStudentId.text.toString().trim(),
                password = binding.etPassword.text.toString().trim(),
                confirmPassword = binding.etConfirmPassword.text.toString().trim()
            )
        }
        binding.tvLoginLink.setOnClickListener { finish() }
    }

    // ─── RegisterContract.View Implementation ──────────────────

    override fun showLoading(show: Boolean) {
        binding.btnRegister.isEnabled = !show
        binding.btnRegister.text = if (show) "" else getString(R.string.btn_register)
        if (show) binding.progressRegister.show() else binding.progressRegister.hide()
    }

    override fun showError(message: String) {
        binding.root.snackbarError(message)
    }

    override fun showNetworkError() {
        binding.root.snackbarError(getString(R.string.error_no_internet))
    }

    override fun handleUnauthorized() {
        // Not applicable for register screen
    }

    override fun onRegisterSuccess() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.fade_in, 0)
    }

    override fun setFullNameError(message: String?) {
        binding.tilFullName.error = message
    }

    override fun setEmailError(message: String?) {
        binding.tilEmail.error = message
    }

    override fun setStudentIdError(message: String?) {
        binding.tilStudentId.error = message
    }

    override fun setPasswordError(message: String?) {
        binding.tilPassword.error = message
    }

    override fun setConfirmPasswordError(message: String?) {
        binding.tilConfirmPassword.error = message
    }
}
