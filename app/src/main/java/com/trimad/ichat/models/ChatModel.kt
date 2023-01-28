package com.trimad.ichat.models

import com.google.firebase.Timestamp

data class ChatModel(
    val chat_id: String? = null,
    val user_name: String? = null,
    val type: String? = null,
    val timestamp: Timestamp? = null,
    val message:String?=null,
    var user_one_name: String? = null,  // username will be group name or individaual person name
    var user_two_name: String? = null,  // username will be group name or individaual person name
    var sender_id: String? = null,
    var receiver_id: String? = null,
    )
