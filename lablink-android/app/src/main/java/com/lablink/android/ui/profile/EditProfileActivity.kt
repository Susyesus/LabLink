package com.lablink.android.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lablink.android.R
import com.lablink.android.data.api.RetrofitClient
import com.lablink.android.data.local.SessionManager
import com.lablink.android.data.model.ApiResponse
import com.lablink.android.data.model.UpdateProfileRequest
import com.lablink.android.data.model.UserProfile
import com.lablink.android.databinding.ActivityEditProfileBinding
import com.lablink.android.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        setupListeners()
        loadCurrentProfile()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveProfile() }
    }

    private fun loadCurrentProfile() {
        binding.progressSave.show()
        binding.btnSave.isEnabled = false

        RetrofitClient.instance.getProfile().enqueue(object : Callback<ApiResponse<UserProfile>> {
            override fun onResponse(
                call: Call<ApiResponse<UserProfile>>,
                response: Response<ApiResponse<UserProfile>>
            ) {
                binding.progressSave.hide()
                binding.btnSave.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()!!.data!!
                    binding.etFullName.setText(profile.fullName)
                    binding.etStudentId.setText(profile.idNumber ?: "")
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserProfile>>, t: Throwable) {
                binding.progressSave.hide()
                binding.btnSave.isEnabled = true
                // Pre-fill from cached data instead
                binding.etFullName.setText(sessionManager.getUserName())
                binding.etStudentId.setText(sessionManager.getUserIdNumber() ?: "")
            }
        })
    }

    private fun saveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val studentId = binding.etStudentId.text.toString().trim()

        // Validation
        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            return
        }
        binding.tilFullName.error = null

        if (studentId.isNotEmpty() && !studentId.matches(Regex("^\\d{2}-\\d{4}-\\d{3}$"))) {
            binding.tilStudentId.error = getString(R.string.error_invalid_student_id)
            return
        }
        binding.tilStudentId.error = null

        if (!NetworkUtils.isConnected(this)) {
            binding.root.snackbarError(getString(R.string.error_no_internet))
            return
        }

        showLoading(true)

        val request = UpdateProfileRequest(
            fullName = fullName,
            idNumber = studentId.ifEmpty { null }
        )

        RetrofitClient.instance.updateProfile(request).enqueue(object : Callback<ApiResponse<UserProfile>> {
            override fun onResponse(
                call: Call<ApiResponse<UserProfile>>,
                response: Response<ApiResponse<UserProfile>>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()!!.data!!
                    sessionManager.updateUserName(profile.fullName)
                    binding.root.snackbarSuccess(getString(R.string.profile_updated))

                    // Go back after a short delay
                    binding.root.postDelayed({ finish() }, 1200)
                } else {
                    binding.root.snackbarError(parseErrorMessage(response))
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserProfile>>, t: Throwable) {
                showLoading(false)
                if (!NetworkUtils.isConnected(this@EditProfileActivity)) {
                    binding.root.snackbarError(getString(R.string.error_no_internet))
                } else {
                    binding.root.snackbarError(getString(R.string.error_network))
                }
            }
        })
    }

    private fun showLoading(loading: Boolean) {
        binding.btnSave.isEnabled = !loading
        binding.btnSave.text = if (loading) "" else getString(R.string.btn_save)
        if (loading) binding.progressSave.show() else binding.progressSave.hide()
    }
}
