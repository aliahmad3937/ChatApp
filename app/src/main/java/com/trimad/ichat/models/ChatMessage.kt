package com.trimad.ichat.models

import com.google.firebase.Timestamp

/**
 * Created by Usman Liaqat on 24,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
data class ChatMessage(
    var message_id: String? = null,
    var message_: String? = null,
    var group_id: String? = null, // group_id
    var message_type: String? = null, // group or individual
    var sender_id: String? = null, // sender_user
    var receiver_list: ArrayList<String>? = null, // sender_user
    var msg_receivers: MutableMap<String,String>? = null,
    var timestamp: Timestamp? = null,
    var message_staus: String? = null, // sent,delivered,read
    var sender_name: String? = null, // sent,delivered,read
    var sender_image: String? = null, // sent,delivered,read

)
