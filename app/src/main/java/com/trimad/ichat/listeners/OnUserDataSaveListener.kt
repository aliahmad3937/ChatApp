package com.trimad.ichat.listeners

interface OnUserDataSaveListener {
    fun onTaskSuccess()
    fun onTaskFailure(message: String?)
}