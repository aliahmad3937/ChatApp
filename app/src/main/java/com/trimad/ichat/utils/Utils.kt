package com.trimad.ichat.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.models.UserModel
import com.google.gson.Gson
import java.util.regex.Pattern

object Utils {
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun getProgressDialog(
        context: Context?,
        message: String?
    ): KProgressHUD {
        return KProgressHUD.create(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setLabel(message)
    }

    fun saveUserProfile(context: Context, userProfile: UserModel?) {
        val gson = Gson()
        val json = gson.toJson(userProfile)
        SharedPref.getInstance(context)!!.APP_USER = json
    }

//    fun saveUserProfile2(context: Context, userProfile: UserModel?) {
//        SharedPref.getInstance(context)!!.saveUser(userProfile!!)
//    }

    fun clearUserProfile(context: Context) {
        SharedPref.getInstance(context)!!.clearUserProfile()
    }
//
//    fun getUserProfile(context: Context): UserModel {
//        val gson = Gson()
//        val json = SharedPref.getInstance(context)!!.APP_USER
//        return gson.fromJson(json, UserModel::class.java)
//    }

//    fun getUserProfile2(context: Context): UserModel {
//         return SharedPref.getInstance(context)!!.getUser()
//    }
}