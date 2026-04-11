package com.lablink.android.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.network.RetrofitClient
import com.lablink.android.core.util.*
import com.lablink.android.databinding.ActivityLoginBinding
import com.lablink.android.feature.auth.contract.LoginContract
import com.lablink.android.feature.auth.data.AuthRepository
import com.lablink.android.feature.auth.presenter.LoginPresenter
import com.lablink.android.feature.equipment.ui.DashboardActivity

/**
 * Login screen — thin MVP View implementation.
 * All business logic is delegated to [LoginPresenter].
 */
class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        val repository = AuthRepository(RetrofitClient.instance, sessionManager)
        presenter = LoginPresenter(repository, this)
        presenter.attachView(this)

        setupUI()
        setupListeners()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun setupUI() {
        // Animate form elements
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.tilEmail.startAnimation(slideUp)
        binding.tilPassword.startAnimation(slideUp)
        binding.btnLogin.startAnimation(slideUp)
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            presenter.login(email, password)
        }
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // ─── LoginContract.View Implementation ─────────────────────

    override fun showLoading(show: Boolean) {
        binding.btnLogin.isEnabled = !show
        binding.btnLogin.text = if (show) "" else getString(R.string.btn_login)
        if (show) binding.progressLogin.show() else binding.progressLogin.hide()
    }

    override fun showError(message: String) {
        binding.root.snackbarError(message)
    }

    override fun showNetworkError() {
        binding.root.snackbarError(getString(R.string.error_no_internet))
    }

    override fun handleUnauthorized() {
        // Not applicable for login screen
    }

    override fun onLoginSuccess() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.fade_in, 0)
    }

    override fun setEmailError(message: String?) {
        binding.tilEmail.error = message
    }
}
