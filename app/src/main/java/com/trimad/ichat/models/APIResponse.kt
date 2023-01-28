package com.trimad.ichat.models

sealed class APIResponse{
    data class onTaskSuccess(val group_list: ArrayList<GroupModel>? ,val user_list: ArrayList<HomeChatModel>? ,val chat_list: ArrayList<ChatMessage>?, val myGroupList:ArrayList<String>?) : APIResponse()
    data class onTaskError(val message: String) : APIResponse()
    object onTaskEmpty : APIResponse()
    object Loading : APIResponse()
}
