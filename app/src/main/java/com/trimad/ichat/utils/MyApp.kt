package com.trimad.ichat.utils

import android.app.Application
import com.trimad.ichat.models.GroupModel
import com.trimad.ichat.models.HomeChatModel
import com.trimad.ichat.models.UserModel

class MyApp {


    companion object{
        var userModel:UserModel? = null
        var isCheckUserGroups = false
        var isCheckUserChats = false
        var group_list: ArrayList<GroupModel> = ArrayList<GroupModel>()
        var myGroup_list: ArrayList<String> = ArrayList<String>()
        var homeChatList: ArrayList<HomeChatModel> = ArrayList()
    }



}