package com.lablink.android.feature.profile.presenter

import android.content.Context
import android.graphics.BitmapFactory
import com.lablink.android.core.base.BasePresenter
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.util.NetworkUtils
import com.lablink.android.core.util.parseErrorMessage
import com.lablink.android.feature.profile.contract.ProfileContract
import com.lablink.android.feature.profile.data.ProfileRepository
import com.lablink.android.feature.profile.model.UserProfile
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Presenter for the Profile screen.
 * Handles profile loading, photo loading, and logout logic.
 */
class ProfilePresenter(
    private val repository: ProfileRepository,
    private val context: Context
) : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {

    override fun loadProfile() {
        view?.showLoading(true)

        repository.getProfile(object : retrofit2.Callback<ApiResponse<UserProfile>> {
            override fun onResponse(
                call: retrofit2.Call<ApiResponse<UserProfile>>,
                response: retrofit2.Response<ApiResponse<UserProfile>>
            ) {
                if (!isViewAttached) return
                view?.showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()!!.data!!
                    repository.updateUserName(profile.fullName)
                    view?.displayProfile(profile)

                    if (profile.hasPhoto) {
                        loadProfilePhoto()
                    }
                } else if (response.code() == 401) {
                    view?.handleUnauthorized()
                } else {
                    view?.showError(parseErrorMessage(response))
                }
            }

            override fun onFailure(
                call: retrofit2.Call<ApiResponse<UserProfile>>,
                t: Throwable
            ) {
                if (!isViewAttached) return
                view?.showLoading(false)

                if (!NetworkUtils.isConnected(context)) {
                    view?.showNetworkError()
                } else {
                    view?.showError("A network error occurred. Please try again.")
                }
            }
        })
    }

    override fun loadProfilePhoto() {
        val token = repository.getAuthToken() ?: return
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(repository.getProfilePhotoUrl())
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Silently fail — initial letter stays visible
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes()
                    if (bytes != null && bytes.isNotEmpty()) {
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bitmap != null && isViewAttached) {
                            view?.showProfilePhoto(bitmap)
                        }
                    }
                }
            }
        })
    }

    override fun logout() {
        repository.logout(object : retrofit2.Callback<ApiResponse<Void>> {
            override fun onResponse(
                call: retrofit2.Call<ApiResponse<Void>>,
                response: retrofit2.Response<ApiResponse<Void>>
            ) {
                // Fire and forget
            }

            override fun onFailure(
                call: retrofit2.Call<ApiResponse<Void>>,
                t: Throwable
            ) {
                // Fire and forget
            }
        })

        repository.clearSession()
        view?.onLogoutComplete()
    }
}
