package com.lablink.android.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lablink.android.R
import com.lablink.android.data.api.RetrofitClient
import com.lablink.android.data.local.SessionManager
import com.lablink.android.data.model.ApiResponse
import com.lablink.android.data.model.UserProfile
import com.lablink.android.databinding.ActivityProfileBinding
import com.lablink.android.ui.auth.LoginActivity
import com.lablink.android.util.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        setupListeners()
        loadProfile()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
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
                .setPositiveButton("Sign Out") { _, _ -> performLogout() }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    private fun loadProfile() {
        binding.progressProfile.show()

        RetrofitClient.instance.getProfile().enqueue(object : Callback<ApiResponse<UserProfile>> {
            override fun onResponse(
                call: Call<ApiResponse<UserProfile>>,
                response: Response<ApiResponse<UserProfile>>
            ) {
                binding.progressProfile.hide()

                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()!!.data!!
                    displayProfile(profile)
                } else if (response.code() == 401) {
                    handleUnauthorized()
                } else {
                    binding.root.snackbarError(parseErrorMessage(response))
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserProfile>>, t: Throwable) {
                binding.progressProfile.hide()
                if (!NetworkUtils.isConnected(this@ProfileActivity)) {
                    binding.root.snackbarError(getString(R.string.error_no_internet))
                } else {
                    binding.root.snackbarError(getString(R.string.error_network))
                }
            }
        })
    }

    private fun displayProfile(profile: UserProfile) {
        // Avatar initial letter
        val initial = profile.fullName.firstOrNull()?.uppercase() ?: "?"
        binding.tvAvatarInitial.text = initial

        // Load profile photo if user has one
        if (profile.hasPhoto) {
            loadProfilePhoto()
        }

        // Hero section
        binding.tvName.text = profile.fullName
        binding.tvEmail.text = profile.email
        binding.tvRole.text = profile.role

        // Info card rows
        binding.tvInfoName.text = profile.fullName
        binding.tvInfoEmail.text = profile.email
        binding.tvStudentId.text = profile.idNumber ?: "Not set"
        binding.tvInfoRole.text = profile.role

        // Update cached name
        sessionManager.updateUserName(profile.fullName)
    }

    private fun loadProfilePhoto() {
        val token = sessionManager.getToken() ?: return
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/api/v1/users/me/photo")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Silently fail — initial letter stays visible
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes()
                    if (bytes != null && bytes.isNotEmpty()) {
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bitmap != null) {
                            runOnUiThread {
                                binding.ivAvatar.setImageBitmap(bitmap)
                                binding.ivAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
                                binding.ivAvatar.show()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun performLogout() {
        RetrofitClient.instance.logout().enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {}
            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {}
        })

        sessionManager.clearSession()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun handleUnauthorized() {
        sessionManager.clearSession()
        toast(getString(R.string.error_session_expired))
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
