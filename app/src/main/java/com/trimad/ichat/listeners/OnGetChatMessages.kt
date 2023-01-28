package com.trimad.ichat.listeners

import com.trimad.ichat.models.ChatMessage

/**
 * Created by Usman Liaqat on 25,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
interface OnGetChatMessages {
    fun onTaskSuccess(chatMessageList: List<ChatMessage>)
    fun onTaskError(message: String?)
    fun onTaskEmpty()
}