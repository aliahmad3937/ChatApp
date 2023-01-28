package com.trimad.ichat.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.trimad.ichat.models.UserModel

object SavedPreference {

    const val EMAIL = "email"
    const val USERID = "userid"
    const val TOKEN = "device_token"
    const val FIRSTNAME = "firstname"
    const val LastNAME = "lastname"
    const val DOB = "dob"
    const val GENDER = "gender"
    const val PHONE = "phone"
    const val COUNTRY = "country"
    const val TOTALTIME = "total_time"
    const val TOTALPOINTS = "total_points"
    const val EXCHANGEPOINTS = "exchange_points"
    const val USERSUBSCRIPTION = "user_subscription"
    const val USERRANK = "user_rank"
    const val EARNINGPOINTS = "earning_point"
    const val CHAMPIONNAME = "user_name"
    const val CHAMPIONCOUNTRY = "ch_country"
    const val CHAMPIONPOINTS = "points"
    const val USERDATA = "ichatuser"


    private fun getSharedPreference(ctx: Context?): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    private fun editor(context: Context, key: String, value: String) {
        getSharedPreference(
            context
        )?.edit()?.putString(key, value)?.apply()
    }

    private fun editor(context: Context, key: String, value: Int) {
        getSharedPreference(
            context
        )?.edit()?.putInt(key, value)?.apply()
    }


    fun setUserID(context: Context, userid: Int) {
        editor(
            context = context,
            key = USERID,
            value = userid
        )
    }

//    fun getUserID(context: Context): Int {
//        val value:UserResponse.Data? = getUserData(context)
//        return if (value == null) 0 else value.id!!
//    }
//    fun getUserName(context: Context): String? {
//        val value:UserResponse.Data? = getUserData(context)
//        return if (value == null) "User name" else "${value.firstname} ${value.lastname}"
//    }
//    fun getUserEmail(context: Context): String {
//        val value:UserResponse.Data? = getUserData(context)
//        return if (value == null) "" else "${value.email}"
//    }

    fun setToken(context: Context, token: String) {
        editor(
            context = context,
            key = TOKEN,
            value = token
        )
    }

    fun getToken(context: Context) = getSharedPreference(
        context
    )?.getString(TOKEN, "")


    fun setUserData(context: Context, data: UserModel) {
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(data)
        //Save that String in SharedPreferences
        getSharedPreference(context)?.edit()?.putString(USERDATA, jsonString)?.apply()
    }

    fun getUserData(context: Context): UserModel? {
        //We read JSON String which was saved.
        val value = getSharedPreference(context)?.getString(USERDATA, null)
        return if (value == null) null else GsonBuilder().create().fromJson(value, UserModel::class.java)
    }

    fun clearUserData(context: Context) {
        getSharedPreference(
            context
        )?.edit()?.remove(USERDATA)?.apply()
    }


    fun removeUserID(context: Context) {
        getSharedPreference(
            context
        )?.edit()?.remove(USERID)?.apply()
    }

    fun clearPreferences(context: Context) {
        getSharedPreference(
            context
        )?.edit()?.clear()?.apply()
    }
}