package com.trimad.ichat.models

import com.google.firebase.Timestamp

/**
 * Created by Usman Liaqat on 01,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
data class ChatGroupInfoModel(
    var user_name: String? = null,  // username will be group name or individaual person name
    var message: String? = null,  // latest message text
    var chat_id: String? = null,  /// group id or receiver id
    var type: String? = null, // group or single
    var group_img: String? = null, // group or single
    var timestamp: Timestamp? = null, // latest message timestamp

)
