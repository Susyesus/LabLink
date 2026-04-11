package com.lablink.android.feature.profile.presenter

import android.content.Context
import com.lablink.android.R
import com.lablink.android.core.base.BasePresenter
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.util.NetworkUtils
import com.lablink.android.core.util.parseErrorMessage
import com.lablink.android.feature.profile.contract.ChangePasswordContract
import com.lablink.android.feature.profile.data.ProfileRepository
import com.lablink.android.feature.profile.model.ChangePasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Presenter for the Change Password screen.
 * Handles multi-field validation, network checks, and password change API calls.
 */
class ChangePasswordPresenter(
    private val repository: ProfileRepository,
    private val context: Context
) : BasePresenter<ChangePasswordContract.View>(), ChangePasswordContract.Presenter {

    override fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        // ── Validation ─────────────────────────────────────────────
        var hasError = false

        if (currentPassword.isEmpty()) {
            view?.setCurrentPasswordError("Current password is required")
            hasError = true
        } else {
            view?.setCurrentPasswordError(null)
        }

        if (newPassword.isEmpty()) {
            view?.setNewPasswordError("New password is required")
            hasError = true
        } else if (newPassword.length < 8) {
            view?.setNewPasswordError(context.getString(R.string.error_password_length))
            hasError = true
        } else {
            view?.setNewPasswordError(null)
        }

        if (confirmPassword.isEmpty()) {
            view?.setConfirmPasswordError("Please confirm your new password")
            hasError = true
        } else if (newPassword != confirmPassword) {
            view?.setConfirmPasswordError(context.getString(R.string.error_passwords_mismatch))
            hasError = true
        } else {
            view?.setConfirmPasswordError(null)
        }

        if (hasError) return

        if (!NetworkUtils.isConnected(context)) {
            view?.showNetworkError()
            return
        }

        view?.showLoading(true)

        val request = ChangePasswordRequest(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )

        repository.changePassword(request, object : Callback<ApiResponse<Void>> {
            override fun onResponse(
                call: Call<ApiResponse<Void>>,
                response: Response<ApiResponse<Void>>
            ) {
                if (!isViewAttached) return
                view?.showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    view?.clearFields()
                    view?.onPasswordChanged()
                } else if (response.code() == 401) {
                    view?.handleUnauthorized()
                } else {
                    view?.showError(parseErrorMessage(response))
                }
            }

            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
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
}
