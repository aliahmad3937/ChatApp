package com.trimad.ichat.databasecontroller

import com.trimad.ichat.listeners.OnUserDataSaveListener
import com.google.firebase.firestore.SetOptions
import com.trimad.ichat.models.GroupModel
import com.google.firebase.Timestamp
import java.util.HashMap

class DatabaseUploader {

    companion object {
        private const val TAG = "DatabaseUploader"

        fun saveGroup(group: GroupModel, onUserDataSaveListener: OnUserDataSaveListener) {
            DatabaseAddresses.getSingleGroupsRefrence(group.group_id.toString())
                .set(group, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onUserDataSaveListener.onTaskSuccess()
                    }
                }
                .addOnFailureListener { e -> onUserDataSaveListener.onTaskFailure(e.localizedMessage) }
        }

        fun updateToken(
            userId: String?,
            token: String,
            last_seen: Timestamp,
            isOnline: Boolean,
            onUserDataSaveListener: OnUserDataSaveListener
        )
        {
            val docData: MutableMap<String, Any> = HashMap()
            docData["user_token"] = token
            docData["last_seen"] = last_seen
            docData["online"] = isOnline
            DatabaseAddresses.getSingleUserReference(userId)
                .set(docData, SetOptions.merge()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onUserDataSaveListener.onTaskSuccess()
                    }
                }
                .addOnFailureListener { e -> onUserDataSaveListener.onTaskFailure(e.localizedMessage) }
        }


        fun updateToken(
            userId: String?,
            token: String,
            last_seen: Timestamp,
            isOnline: Boolean,
        )
        {
            val docData: MutableMap<String, Any> = HashMap()
            docData["user_token"] = token
            docData["last_seen"] = last_seen
            docData["online"] = isOnline
            DatabaseAddresses.getSingleUserReference(userId)
                .set(docData, SetOptions.merge())
        }


        fun updateStatus(
            userId: String?,
            last_seen: Timestamp,
            isOnline: Boolean,
            onUserDataSaveListener: OnUserDataSaveListener
        ) {
            val docData: MutableMap<String, Any> = HashMap()
            docData["last_seen"] = last_seen
            docData["online"] = isOnline
            DatabaseAddresses.getSingleUserReference(userId)
                .set(docData, SetOptions.merge()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onUserDataSaveListener.onTaskSuccess()
                    }
                }
                .addOnFailureListener { e -> onUserDataSaveListener.onTaskFailure(e.localizedMessage) }
        }

    }
}