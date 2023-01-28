package com.trimad.ichat.models

import com.google.firebase.Timestamp;

data class HomeChatModel (
    val user_id: String? = null,
    val doc_id: String? = null,
    val user_name: String? = null,
    val name1: String? = null,
    val name2: String? = null,
    val image1: String? = null,
    val image2: String? = null,
    val user_bio: String? = null,
    val user_email: String? = null,
    val user_image: String? = null,
    val user_token: String? = null,
    val online: Boolean? = null,
    val user_active: Boolean? = null,
    val last_seen: Timestamp? = null,
    val organization_id:String?=null,
    var msg_count:MutableList<ChatMessage> = arrayListOf()
)
