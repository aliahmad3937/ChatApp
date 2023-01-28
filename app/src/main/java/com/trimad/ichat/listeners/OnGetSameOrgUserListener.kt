package com.trimad.ichat.listeners

import com.trimad.ichat.models.UserModel

/**
 * Created by Usman Liaqat on 18,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
interface OnGetSameOrgUserListener {

    fun onTaskSuccess(userModelList: List<UserModel>)
    fun onTaskError(message: String?)
    fun onTaskEmpty()
}