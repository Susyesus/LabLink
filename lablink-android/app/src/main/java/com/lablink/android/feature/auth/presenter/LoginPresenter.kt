package com.lablink.android.feature.auth.presenter

import android.content.Context
import com.lablink.android.core.base.BasePresenter
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.util.NetworkUtils
import com.lablink.android.core.util.parseErrorMessage
import com.lablink.android.feature.auth.contract.LoginContract
import com.lablink.android.feature.auth.data.AuthRepository
import com.lablink.android.feature.auth.model.AuthData
import com.lablink.android.feature.auth.model.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Presenter for the Login screen.
 * Handles input validation, network checks, and login API calls.
 */
class LoginPresenter(
    private val repository: AuthRepository,
    private val context: Context
) : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    override fun login(email: String, password: String) {
        // Client-side validation
        if (email.isEmpty() || password.isEmpty()) {
            view?.showError("Please fill in all required fields.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.setEmailError("Please enter a valid email address.")
            return
        }
        view?.setEmailError(null)

        // Check connectivity
        if (!NetworkUtils.isConnected(context)) {
            view?.showNetworkError()
            return
        }

        view?.showLoading(true)

        val request = LoginRequest(email, password)
        repository.login(request, object : Callback<ApiResponse<AuthData>> {
            override fun onResponse(
                call: Call<ApiResponse<AuthData>>,
                response: Response<ApiResponse<AuthData>>
            ) {
                if (!isViewAttached) return
                view?.showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    repository.saveSession(authData)
                    view?.onLoginSuccess()
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
