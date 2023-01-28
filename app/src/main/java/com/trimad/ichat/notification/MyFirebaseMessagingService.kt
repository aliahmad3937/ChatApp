package com.trimad.ichat.notification

import android.app.Notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.trimad.ichat.R
import org.json.JSONObject
import org.json.JSONException
import android.os.Bundle
import com.trimad.ichat.constants.ConstantsData
import androidx.navigation.NavDeepLinkBuilder
import com.trimad.ichat.ui.activities.MainActivity
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.app.PendingIntent
import android.util.Log
import java.lang.Exception

/**
 * Created by Kamran on 01/11/20.
 */
class MyFirebaseMessagingService : FirebaseMessagingService()
{
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: ")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            sendNotification(remoteMessage.data)
        }
    }

    fun getValue(data: Map<String?, String?>, key: String?): String? {
        return try {
            if (data.containsKey(key)) data[key] else getString(R.string.app_name)
        } catch (ex: Exception) {
            ex.printStackTrace()
            getString(R.string.app_name)
        }
    }

    override fun onNewToken(token: String) {}
    private fun sendNotification(messageBody: Map<String?, String?>) {
        var message = ""
        var mType = ""
        var groupid = ""
        var senderid = ""
        var receiverid = ""
        val `object` = JSONObject(messageBody)
        try {
            Log.d(TAG, "sendNotification: notificationdata" + `object`.getString("message"))

            message = `object`.getString("message")
            mType = `object`.getString("type")  /// message_group_type, message_individual_type
            receiverid = `object`.getString("receiverid")  /// message_group_type, message_individual_type

            groupid = `object`.getString("groupid")
            senderid = `object`.getString(ConstantsData.chatSenderId)
            Log.i("TAG4", "firebase 1: group_id${`object`.getString(ConstantsData.chatGroupId)}")
            Log.i("TAG4", "firebase 1 bundle: group_id${"groupid"}")

        /// who has send message
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val bundle = Bundle()
       // bundle.putString("group_id", groupid)
//        bundle.putString(ConstantsData.spaceChatId, senderid)

        if(mType == "single"){
            bundle.putString("type", mType)
            bundle.putString("receiver_id", receiverid)
            bundle.putString("group_id", groupid)
        }else{
            bundle.putString("type", mType)
            bundle.putString("receiver_id", receiverid)
            bundle.putString("group_id", receiverid)
        }

        Log.i("TAG4", "firebase: group_id$groupid")
        Log.i("TAG4", "firebase bundle: group_id${bundle.getString("group_id")}")

        var pendingIntent: PendingIntent? =null;

        if (mType.equals(ConstantsData.message_single_type))
        {
            pendingIntent = NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.singleLiveChatFragment)
                .setArguments(bundle)
                .createPendingIntent()
        }else{
            pendingIntent = NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.liveGroupChatFragment)
                .setArguments(bundle)
                .createPendingIntent()
        }


        val channelId = "ChatApp"
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("ChatApp New Message")
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentText(message).setAutoCancel(true).setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setShowBadge(true)
            channel.setSound(soundUri, audioAttributes)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(channel)
        }
        manager.notify(0, builder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}