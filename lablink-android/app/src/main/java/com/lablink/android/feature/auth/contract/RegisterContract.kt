package com.lablink.android.feature.auth.contract

import com.lablink.android.core.base.BaseView

/**
 * MVP Contract for the Register screen.
 * Defines the communication interface between RegisterActivity (View) and RegisterPresenter.
 */
interface RegisterContract {

    interface View : BaseView {
        /** Called when registration succeeds — navigate to Dashboard */
        fun onRegisterSuccess()

        /** Set or clear field-specific errors */
        fun setFullNameError(message: String?)
        fun setEmailError(message: String?)
        fun setStudentIdError(message: String?)
        fun setPasswordError(message: String?)
        fun setConfirmPasswordError(message: String?)
    }

    interface Presenter {
        /** Attempt registration with the given form data */
        fun register(
            fullName: String,
            email: String,
            studentId: String,
            password: String,
            confirmPassword: String
        )
    }
}
