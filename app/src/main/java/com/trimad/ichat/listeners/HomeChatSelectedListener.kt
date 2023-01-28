package com.trimad.ichat.listeners

import com.trimad.ichat.models.HomeChatModel
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.ui.adapters.ChatHomeAdapter

interface HomeChatSelectedListener {
    fun onChatSelect(userModel: HomeChatModel)
}