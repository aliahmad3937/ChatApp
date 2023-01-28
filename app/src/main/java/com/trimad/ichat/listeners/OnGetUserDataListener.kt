package com.trimad.ichat.listeners

import com.trimad.ichat.models.UserModel

interface OnGetUserDataListener {
    fun onTaskSuccess(userModel: UserModel?)
    fun onTaskError(message: String?)
    fun onTaskEmpty()
}