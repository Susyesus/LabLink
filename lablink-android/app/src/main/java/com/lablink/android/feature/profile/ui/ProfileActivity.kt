package com.lablink.android.feature.profile.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lablink.android.R
import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.network.RetrofitClient
import com.lablink.android.core.util.*
import com.lablink.android.databinding.ActivityProfileBinding
import com.lablink.android.feature.auth.ui.LoginActivity
import com.lablink.android.feature.profile.contract.ProfileContract
import com.lablink.android.feature.profile.data.ProfileRepository
import com.lablink.android.feature.profile.model.UserProfile
import com.lablink.android.feature.profile.presenter.ProfilePresenter

/**
 * Profile screen — thin MVP View implementation.
 * All business logic is delegated to [ProfilePresenter].
 */
class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        val repository = ProfileRepository(RetrofitClient.instance, sessionManager)
        presenter = ProfilePresenter(repository, this)
        presenter.attachView(this)

        setupListeners()
        presenter.loadProfile()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadProfile()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out") { _, _ -> presenter.logout() }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    // ─── ProfileContract.View Implementation ───────────────────

    override fun showLoading(show: Boolean) {
        if (show) binding.progressProfile.show() else binding.progressProfile.hide()
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

    override fun displayProfile(profile: UserProfile) {
        // Avatar initial letter
        val initial = profile.fullName.firstOrNull()?.uppercase() ?: "?"
        binding.tvAvatarInitial.text = initial

        // Hero section
        binding.tvName.text = profile.fullName
        binding.tvEmail.text = profile.email
        binding.tvRole.text = profile.role

        // Info card rows
        binding.tvInfoName.text = profile.fullName
        binding.tvInfoEmail.text = profile.email
        binding.tvStudentId.text = profile.idNumber ?: "Not set"
        binding.tvInfoRole.text = profile.role
    }

    override fun showProfilePhoto(bitmap: Bitmap) {
        runOnUiThread {
            binding.ivAvatar.setImageBitmap(bitmap)
            binding.ivAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.ivAvatar.show()
        }
    }

    override fun onLogoutComplete() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
