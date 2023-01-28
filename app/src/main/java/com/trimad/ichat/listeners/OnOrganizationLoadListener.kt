package com.trimad.ichat.listeners

import com.trimad.ichat.models.Organization

/**
 * Created by Usman Liaqat on 31,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
interface OnOrganizationLoadListener {
    fun onTaskSuccess(organization: Organization?)
    fun onTaskError(message: String?)
    fun onTaskEmpty()
}