package com.trimad.ichat.utils

import android.content.Context
import android.content.SharedPreferences
import com.trimad.ichat.models.UserModel

class SharedPref(private val ctx: Context) {
    val USERNAME:String = "username"
    val USERID:String = "userId"
    val ORGID:String = "orgId"
    val TOKEN:String = "userToken"


    fun clearUserProfile() {
        sharedPreferences.edit().clear().apply()
    }

    private val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(PREFERENCE, 0)

    private val USER = "user"

    var APP_USER: String?
        get() = sharedPreferences.getString(USER, "")
        set(app_user) {
            app_user?.let { sharedPreferences.edit().putString(USER, it).apply() }
        }


    fun saveUser(userProfile: UserModel) {
        sharedPreferences.edit().apply {
            putString(USERNAME,userProfile.user_name)
            putString(USERID,userProfile.user_id)
            putString(ORGID,userProfile.organization_id)
            putString(TOKEN,userProfile.user_token)
        }.apply()
    }

    fun getUser() : UserModel {
        return UserModel(
            user_id = sharedPreferences.getString(USERID, ""),
            user_name = sharedPreferences.getString(USERNAME, ""),
            organization_id = sharedPreferences.getString(ORGID, ""),
            user_token = sharedPreferences.getString(TOKEN, ""),
        )
    }


    companion object {
        private var instance: SharedPref? = null
        var PREFERENCE = "ChatApp"
        fun getInstance(context: Context): SharedPref? {
            if (instance == null) {
                instance = SharedPref(context)
            }
            return instance
        }
    }

}