package com.trimad.ichat.listeners

import com.trimad.ichat.models.UserModel

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
interface UserRemoveListener {
    fun onUserRemove(userModel: UserModel)
}