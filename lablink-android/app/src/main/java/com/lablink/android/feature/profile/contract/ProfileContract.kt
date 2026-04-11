package com.lablink.android.feature.profile.contract

import android.graphics.Bitmap
import com.lablink.android.core.base.BaseView
import com.lablink.android.feature.profile.model.UserProfile

/**
 * MVP Contract for the Profile screen.
 * Defines the communication interface between ProfileActivity (View) and ProfilePresenter.
 */
interface ProfileContract {

    interface View : BaseView {
        /** Display the loaded profile data in the UI */
        fun displayProfile(profile: UserProfile)

        /** Display the loaded profile photo bitmap */
        fun showProfilePhoto(bitmap: Bitmap)

        /** Called when logout completes — navigate to Login */
        fun onLogoutComplete()
    }

    interface Presenter {
        /** Load the user's profile from backend */
        fun loadProfile()

        /** Load the user's profile photo */
        fun loadProfilePhoto()

        /** Perform logout (API call + clear session) */
        fun logout()
    }
}
