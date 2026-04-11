package com.lablink.android.feature.profile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.network.RetrofitClient
import com.lablink.android.core.util.*
import com.lablink.android.databinding.ActivityEditProfileBinding
import com.lablink.android.feature.profile.contract.EditProfileContract
import com.lablink.android.feature.profile.data.ProfileRepository
import com.lablink.android.feature.profile.presenter.EditProfilePresenter

/**
 * Edit Profile screen — thin MVP View implementation.
 * All business logic is delegated to [EditProfilePresenter].
 */
class EditProfileActivity : AppCompatActivity(), EditProfileContract.View {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var presenter: EditProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        val repository = ProfileRepository(RetrofitClient.instance, sessionManager)
        presenter = EditProfilePresenter(repository, this)
        presenter.attachView(this)

        setupListeners()
        presenter.loadCurrentProfile()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener {
            presenter.saveProfile(
                fullName = binding.etFullName.text.toString().trim(),
                studentId = binding.etStudentId.text.toString().trim()
            )
        }
    }

    // ─── EditProfileContract.View Implementation ───────────────

    override fun showLoading(show: Boolean) {
        binding.btnSave.isEnabled = !show
        binding.btnSave.text = if (show) "" else getString(R.string.btn_save)
        if (show) binding.progressSave.show() else binding.progressSave.hide()
    }

    override fun showError(message: String) {
        binding.root.snackbarError(message)
    }

    override fun showNetworkError() {
        binding.root.snackbarError(getString(R.string.error_no_internet))
    }

    override fun handleUnauthorized() {
        // Handled at profile level
    }

    override fun populateForm(fullName: String, studentId: String) {
        binding.etFullName.setText(fullName)
        binding.etStudentId.setText(studentId)
    }

    override fun onSaveSuccess() {
        binding.root.snackbarSuccess(getString(R.string.profile_updated))
        binding.root.postDelayed({ finish() }, 1200)
    }

    override fun setFullNameError(message: String?) {
        binding.tilFullName.error = message
    }

    override fun setStudentIdError(message: String?) {
        binding.tilStudentId.error = message
    }
}
