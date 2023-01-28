package com.trimad.ichat.databasecontroller

import com.google.firebase.firestore.FirebaseFirestore
import com.trimad.ichat.constants.ConstantsData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

object DatabaseAddresses {
    private const val TAG = "DatabaseAddresses"

    fun getSingleUserReference(userId: String?): DocumentReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.users).document(
            userId!!
        )
    }
    fun getSingleOrganizationReference(org_id: String?): DocumentReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.organizations).document(
            org_id!!
        )
    }
    fun getUsersReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.users)
    }

    fun getGroupsRefrence(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.groups)
    }
    fun getSingleGroupsRefrence(groupId: String): DocumentReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.groups).document(groupId)
    }
    fun getChatMessageRefrence(groupId: String): CollectionReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.chatMessage)
            .document(groupId).collection(ConstantsData.message)
    }

    fun getChatMessageReference(groupId: String , docId:String ): DocumentReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.chatMessage)
            .document(groupId).collection(ConstantsData.message).document(docId)
    }

    fun getChatReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.chatMessage)
    }
    fun getGroupChatReference(groupId: String): CollectionReference {
        return FirebaseFirestore.getInstance().collection(ConstantsData.chatMessage)
            .document(groupId)
            .collection(ConstantsData.message)
    }

}