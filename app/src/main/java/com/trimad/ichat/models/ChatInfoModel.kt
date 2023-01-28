package com.trimad.ichat.models

import com.google.firebase.Timestamp

/**
 * Created by Usman Liaqat on 01,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
data class ChatInfoModel(
    var user_one_name: String? = null,  // username will be group name or individaual person name
    var user_two_name: String? = null,  // username will be group name or individaual person name
    var user_one_image: String? = null,  // username will be group name or individaual person name
    var user_two_image: String? = null,  // username will be group name or individaual person name
    var sender_id: String? = null,
    var receiver_id: String? = null,
    var message: String? = null,  // latest message text
    var chat_id: String? = null,  /// group id or receiver id
    var type: String? = null, // group or single
    var timestamp: Timestamp? = null, // latest message timestamp

)
