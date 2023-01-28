package com.trimad.ichat.listeners

import com.trimad.ichat.models.ChatMessage

interface UnSeenMessageCount {
    fun onMessageCount(pos:Int ,count:List<ChatMessage>)

}