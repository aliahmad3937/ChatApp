package com.trimad.ichat.models

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
data class GroupMember(
    var user_id: String? = null,
    var isAdmin: Boolean? = false,
    var isnotify: Boolean? = true,
    var isadded: Boolean? = true,

)