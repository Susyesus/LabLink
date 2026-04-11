package com.lablink.android.feature.profile.presenter

import android.content.Context
import com.lablink.android.R
import com.lablink.android.core.base.BasePresenter
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.util.NetworkUtils
import com.lablink.android.core.util.parseErrorMessage
import com.lablink.android.feature.profile.contract.EditProfileContract
import com.lablink.android.feature.profile.data.ProfileRepository
import com.lablink.android.feature.profile.model.UpdateProfileRequest
import com.lablink.android.feature.profile.model.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Presenter for the Edit Profile screen.
 * Handles loading existing profile data, validation, and profile update API calls.
 */
class EditProfilePresenter(
    private val repository: ProfileRepository,
    private val context: Context
) : BasePresenter<EditProfileContract.View>(), EditProfileContract.Presenter {

    override fun loadCurrentProfile() {
        view?.showLoading(true)

        repository.getProfile(object : Callback<ApiResponse<UserProfile>> {
            override fun onResponse(
                call: Call<ApiResponse<UserProfile>>,
                response: Response<ApiResponse<UserProfile>>
            ) {
                if (!isViewAttached) return
                view?.showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()!!.data!!
                    view?.populateForm(profile.fullName, profile.idNumber ?: "")
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserProfile>>, t: Throwable) {
                if (!isViewAttached) return
                view?.showLoading(false)
                // Pre-fill from cached data instead
                view?.populateForm(
                    repository.getUserName() ?: "",
                    repository.getUserIdNumber() ?: ""
                )
            }
        })
    }

    override fun saveProfile(fullName: String, studentId: String) {
        // Validation
        if (fullName.isEmpty()) {
            view?.setFullNameError("Full name is required")
            return
        }
        view?.setFullNameError(null)

        if (studentId.isNotEmpty() && !studentId.matches(Regex("^\\d{2}-\\d{4}-\\d{3}$"))) {
            view?.setStudentIdError(context.getString(R.string.error_invalid_student_id))
            return
        }
        view?.setStudentIdError(null)

        if (!NetworkUtils.isConnected(context)) {
            view?.showNetworkError()
            return
        }

        view?.showLoading(true)

        val request = UpdateProfileRequest(
            fullName = fullName,
            idNumber = studentId.ifEmpty { null }
        )

        repository.updateProfile(request, object : Callback<ApiResponse<UserProfile>> {
            override fun onResponse(
                call: Call<ApiResponse<UserProfile>>,
                response: Response<ApiResponse<UserProfile>>
            ) {
                if (!isViewAttached) return
                view?.showLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()!!.data!!
                    repository.updateUserName(profile.fullName)
                    view?.onSaveSuccess()
                } else {
                    view?.showError(parseErrorMessage(response))
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserProfile>>, t: Throwable) {
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
