package com.trimad.ichat.models

/**
 * Created by Usman Liaqat on 25,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
data class ChatReadModel(
    var chat_sender_userModel: UserModel? = null,
    var chatMessage: ChatMessage? = null,
)
