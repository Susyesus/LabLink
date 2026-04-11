package com.lablink.android.feature.auth.contract

import com.lablink.android.core.base.BaseView

/**
 * MVP Contract for the Login screen.
 * Defines the communication interface between LoginActivity (View) and LoginPresenter.
 */
interface LoginContract {

    interface View : BaseView {
        /** Called when login succeeds — navigate to Dashboard */
        fun onLoginSuccess()

        /** Set or clear the email field error */
        fun setEmailError(message: String?)
    }

    interface Presenter {
        /** Attempt login with the given credentials */
        fun login(email: String, password: String)
    }
}
