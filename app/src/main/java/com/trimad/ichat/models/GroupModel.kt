package com.trimad.ichat.models

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
data class GroupModel(
    var group_id: String? = null,
    var group_name: String? = null,
    var group_image: String? = null,
    var users_list:ArrayList<GroupMember>?=null
)
