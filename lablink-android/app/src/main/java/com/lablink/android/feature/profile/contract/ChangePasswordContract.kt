package com.lablink.android.feature.profile.contract

import com.lablink.android.core.base.BaseView

/**
 * MVP Contract for the Change Password screen.
 * Defines the communication interface between ChangePasswordActivity (View) and ChangePasswordPresenter.
 */
interface ChangePasswordContract {

    interface View : BaseView {
        /** Called when password is changed successfully */
        fun onPasswordChanged()

        /** Clear all password input fields */
        fun clearFields()

        /** Set or clear field-specific errors */
        fun setCurrentPasswordError(message: String?)
        fun setNewPasswordError(message: String?)
        fun setConfirmPasswordError(message: String?)
    }

    interface Presenter {
        /** Attempt to change the password */
        fun changePassword(
            currentPassword: String,
            newPassword: String,
            confirmPassword: String
        )
    }
}
