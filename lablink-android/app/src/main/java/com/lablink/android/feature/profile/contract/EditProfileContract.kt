package com.lablink.android.feature.profile.contract

import com.lablink.android.core.base.BaseView

/**
 * MVP Contract for the Edit Profile screen.
 * Defines the communication interface between EditProfileActivity (View) and EditProfilePresenter.
 */
interface EditProfileContract {

    interface View : BaseView {
        /** Populate form fields with existing profile data */
        fun populateForm(fullName: String, studentId: String)

        /** Called when profile is saved successfully */
        fun onSaveSuccess()

        /** Set or clear field-specific errors */
        fun setFullNameError(message: String?)
        fun setStudentIdError(message: String?)
    }

    interface Presenter {
        /** Load the current profile to pre-fill the form */
        fun loadCurrentProfile()

        /** Save the updated profile */
        fun saveProfile(fullName: String, studentId: String)
    }
}
