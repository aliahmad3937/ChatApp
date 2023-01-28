package com.trimad.ichat.listeners

import com.trimad.ichat.models.GroupModel

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
interface OnGetGroupDataListener {
    fun onTaskSuccess(groupModel: GroupModel?)
    fun onTaskError(message: String?)
    fun onTaskEmpty()
}