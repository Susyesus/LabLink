package com.lablink.android.feature.auth.presenter

import android.content.Context
import com.lablink.android.R
import com.lablink.android.core.base.BasePresenter
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.util.NetworkUtils
import com.lablink.android.core.util.parseErrorMessage
import com.lablink.android.feature.auth.contract.RegisterContract
import com.lablink.android.feature.auth.data.AuthRepository
import com.lablink.android.feature.auth.model.AuthData
import com.lablink.android.feature.auth.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Presenter for the Register screen.
 * Handles multi-field validation, network checks, and registration API calls.
 */
class RegisterPresenter(
    private val repository: AuthRepository,
    private val context: Context
) : BasePresenter<RegisterContract.View>(), RegisterContract.Presenter {

    override fun register(
        fullName: String,
        email: String,
        studentId: String,
        password: String,
        confirmPassword: String
    ) {
        // ── Client-side validation ─────────────────────────────────
        var hasError = false

        if (fullName.isEmpty()) {
            view?.setFullNameError("Full name is required")
            hasError = true
        } else {
            view?.setFullNameError(null)
        }

        if (email.isEmpty()) {
            view?.setEmailError("Email is required")
            hasError = true
        } else if (!email.matches(Regex("^[a-zA-Z]+\\.[a-zA-Z]+@cit\\.edu$"))) {
            view?.setEmailError("Email must follow format: firstname.lastname@cit.edu")
            hasError = true
        } else {
            view?.setEmailError(null)
        }

        if (studentId.isNotEmpty() && !studentId.matches(Regex("^\\d{2}-\\d{4}-\\d{3}$"))) {
            view?.setStudentIdError(context.getString(R.string.error_invalid_student_id))
            hasError = true
        } else {
            view?.setStudentIdError(null)
        }

        if (password.isEmpty()) {
            view?.setPasswordError("Password is required")
            hasError = true
        } else if (password.length < 8) {
            view?.setPasswordError(context.getString(R.string.error_password_length))
            hasError = true
        } else {
            view?.setPasswordError(null)
        }

        if (confirmPassword.isEmpty()) {
            view?.setConfirmPasswordError("Please confirm your password")
            hasError = true
        } else if (password != confirmPassword) {
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

        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            idNumber = studentId.ifEmpty { null }
        )

        repository.register(request, object : Callback<ApiResponse<AuthData>> {
            override fun onResponse(
                call: Call<ApiResponse<AuthData>>,
                response: Response<ApiResponse<AuthData>>
            ) {
                if (!isViewAttached) return
                view?.showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    repository.saveSession(authData)
                    view?.onRegisterSuccess()
                } else {
                    val errorMsg = parseErrorMessage(response)
                    view?.showError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ApiResponse<AuthData>>, t: Throwable) {
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
