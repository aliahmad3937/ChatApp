package com.trimad.ichat.constants

interface ConstantsData {
    companion object {

        const val from = "From"


        const val users = "Users"
        const val groups = "Groups"

        const val chatGroupId = "group_id"
        const val chatSenderId = "sender_id"

        // notification type
        const val message_group_type = "group"
        const val message_single_type = "single"

        ///// message status
        const val status_sent="Sent"  // by default single tick
        const val status_deliverd="Delivered"  // double tick
        const val status_seen="Seen"   // blue tick


        const val organizations = "Organizations"
        const val message = "Messages"
        const val chatMessage = "ChatMessages"

        const val FIREBASE_NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send"
        const val FIREBASE_NOTIFICATION_CONTENT_TYPE = "application/json"
        const val FIREBASE_NOTIFICATION_SERVER_KEY =
            "key=AAAAwA5PGHA:APA91bHKhG4MlnY9jeFirKWu1hkjQtKz2ZOj6TWuDkeaB8J66fi3C9Jgzo87uDEIRMN-cf58QEpD7TkKwmg3TiiBiOV275yJk7GqZqGK1EQm5mok8OmrTHvkrDvf-DgnUq5ToCkvBNq6"


    }
}