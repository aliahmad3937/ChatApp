package com.trimad.ichat.databasecontroller

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.trimad.ichat.listeners.*
import com.trimad.ichat.models.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage


/**
 * Created by Usman Liaqat on 17,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
object Repoistory {


    var groupResponse = MutableLiveData<APIResponse>()
    var chatHomeResponse = MutableLiveData<APIResponse>()
    var chatUnSeenResponse = MutableLiveData<APIResponse>()


    fun getUser(userId: String?, onGetUserDataListener: OnGetUserDataListener) {
        DatabaseAddresses.getSingleUserReference(userId)
            .get()
            .addOnSuccessListener{
                if (it.exists()) {
                    onGetUserDataListener.onTaskSuccess(it.toObject(UserModel::class.java)!!)
                 //   Log.d("TAG", "Current data: ${snapshot.data}")
                } else {
                    onGetUserDataListener.onTaskEmpty()
               //     Log.d("TAG", "Current data: null")
                }
            }
            .addOnFailureListener { e ->
                onGetUserDataListener.onTaskError(e.message)
            }
    }


    fun getUser2(userId: String?, onGetUserDataListener: OnGetUserDataListener) {
        DatabaseAddresses.getSingleUserReference(userId)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {

                    var userModel: UserModel = it.toObject(UserModel::class.java)!!
                    onGetUserDataListener.onTaskSuccess(it.toObject(UserModel::class.java)!!)
                    //    Log.d("TAG", "Current data: ${snapshot.data}")
                } else {
                    onGetUserDataListener.onTaskEmpty()
                    Log.d("TAG", "Current data: null")
                }
            }
            .addOnFailureListener {
                onGetUserDataListener.onTaskError(it.message)
            }
    }

    fun getSimpleUser(userId: String?, onGetUserDataListener: OnGetUserDataListener) {

        DatabaseAddresses.getSingleUserReference(userId)
            .get()
            .addOnSuccessListener {
                if (it != null && it.exists()) {

                    val userModel: UserModel = it.toObject(UserModel::class.java)!!
                    onGetUserDataListener.onTaskSuccess(userModel)
                    Log.d("TAG", "Current data: ${it.data}")
                } else {
                    onGetUserDataListener.onTaskEmpty()
                    Log.d("TAG", "Current data: null")
                }
            }.addOnFailureListener {
                onGetUserDataListener.onTaskError(it.message)
            }

    }

    fun getUserByOrganization(org_id: String?, onGetUserDataListener: OnGetSameOrgUserListener) {
        Log.i("TAGTAG", "getUserByOrganization: " + org_id)
        DatabaseAddresses.getUsersReference()
            .whereEqualTo("organization_id", org_id.toString())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user_list = ArrayList<UserModel>()
                    for (document in documents) {
                        val userModel = document.toObject(UserModel::class.java)
                        user_list.add(userModel)

                        Log.i("TAGTAG", "${document.id} => ${document.data}")
                    }
                    onGetUserDataListener.onTaskSuccess(user_list)
                } else {
                    onGetUserDataListener.onTaskEmpty()
                }

            }
            .addOnFailureListener { exception ->
                onGetUserDataListener.onTaskError(exception.message)
                Log.i("TAGTAG", "Error getting documents: ", exception)
            }
    }

    fun getLiveUserByOrganization(
        userId: String,
        org_id: String?,
        context: Context,
        onGetUserDataListener: OnGetSameOrgUserListener
    ) {
        if (org_id == null || org_id == "") {
            getSingleUser(userId, context, onGetUserDataListener)

        } else {
            DatabaseAddresses.getUsersReference()
                .whereEqualTo("organization_id", org_id.toString())
                .whereEqualTo("user_active", true)
                .get()
                .addOnSuccessListener {  value ->

                    val user_list: ArrayList<UserModel> = ArrayList<UserModel>()
                    Log.i("TAG9", "getUserByOrganization: " + value?.size())
                    Log.i("TAG9", "getUserByid: " + org_id)

                    for (doc in value!!) {
                        val userModel: UserModel = doc.toObject(UserModel::class.java)
                        if (doc.id != userId)
                            user_list.add(userModel)
                    }
                    if (user_list.size > 0) {
                        onGetUserDataListener.onTaskSuccess(user_list)
                    } else {
                        onGetUserDataListener.onTaskEmpty()
                    }
                }.addOnFailureListener { e ->
                    onGetUserDataListener.onTaskError(e.localizedMessage)
                }
        }


    }

    fun getSingleUser(
        userId: String,
        context: Context,
        onGetUserDataListener: OnGetSameOrgUserListener
    ) {
        DatabaseAddresses.getSingleUserReference(userId)
            .get()
            .addOnSuccessListener{ snapshot ->

                if (snapshot != null && snapshot.exists()) {

                    val userModel: UserModel = snapshot.toObject(UserModel::class.java)!!

                    //  Utils.saveUserProfile(context,userModel)
                    // Utils.saveUserProfile2(context,userModel)
                    getLiveUserByOrganization(
                        userId,
                        userModel.organization_id,
                        context,
                        onGetUserDataListener
                    )
                    Log.d("TAG9", "Current data: ${snapshot.data}")
                } else {
                    onGetUserDataListener.onTaskEmpty()
                    Log.d("TAG9", "Current data: null")
                }
            }

    }


    // to get all chat of current user
    fun getUserChats(userId: String?, name: String, myGroupList: ArrayList<String>) {
        chatHomeResponse.postValue(APIResponse.Loading)
        DatabaseAddresses.getChatReference()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
                if (e != null) {
                    Log.i("TAG6", "Listen failed.", e)
                    chatHomeResponse.postValue(APIResponse.onTaskError(e.localizedMessage))
                    return@EventListener
                }

                val user_list: ArrayList<HomeChatModel> = ArrayList()
                Log.i("TAG6", "getUserChats: " + value?.size())

                for (doc in value!!) {
//                    val userModel: UserModel = doc.toObject(UserModel::class.java)
//                    user_list.add(userModel)
                    if (doc.get("type").toString() == "single") {
                        Log.i("TAG6", "getUserChats: single : 129   id:${doc.id}")

                        if (doc.get("receiver_id")
                                .toString() == userId || doc.get("sender_id").toString() == userId
                        ) {
                            val userModel = HomeChatModel(
                                user_id = if (doc.get("receiver_id")
                                        .toString() == userId
                                ) doc.get("sender_id").toString() else doc.get("receiver_id")
                                    .toString(),
                                user_name = if (doc.get("user_one_name") == name) doc.get("user_two_name")
                                    .toString() else doc.get("user_one_name").toString(),
                                user_bio = doc.get("message").toString(),
                                user_image = if (doc.get("user_one_name") == name) doc.get("user_two_image")
                                    .toString() else doc.get("user_one_image").toString(),
                                user_token = doc.get("type").toString(),
                                last_seen = doc.get("timestamp") as Timestamp,
                                doc_id = doc.id
                            )
                            user_list.add(userModel)
                        }
                    } else {
                        Log.i("TAG6", "getUserChats: group : 138   id:${doc.id}")
                        if (myGroupList.isNotEmpty() && myGroupList.contains(doc.id)) {
                            val userModel = HomeChatModel(
                                user_id = doc.id,
                                user_name = doc.get("user_name").toString(),
                                user_bio = doc.get("message").toString(),
                                user_image = doc.get("group_img").toString(),
                                user_token = doc.get("type").toString(),
                                last_seen = doc.get("timestamp") as Timestamp,
                                doc_id = doc.id
                            )
                            user_list.add(userModel)
                        }
                    }
                }

                if (user_list.size > 0) {
                    chatHomeResponse.postValue(
                        APIResponse.onTaskSuccess(
                            group_list = null,
                            user_list = user_list,
                            chat_list = null,
                            myGroupList = null
                        )
                    )
                } else {
                    chatHomeResponse.postValue(APIResponse.onTaskEmpty)
                }
            })
    }


//
//// filter the group of current user
//    fun getGroups(user_id: String?, onGroupsLoadListener: OnGroupsLoadListener) {
//        DatabaseAddresses.getGroupsRefrence()
//            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
//                if (e != null)
//                {
//                    Log.i("TAGTAGTAG", "Listen failed.", e)
//                    onGroupsLoadListener.onTaskError(e.localizedMessage)
//                    return@EventListener
//                }
//                val group_list: ArrayList<GroupModel> = ArrayList<GroupModel>()
//                Log.i("TAGTAGTAG", "getGroups: "+group_list)
//                for (doc in value!!) {
//                    val groupModel: GroupModel = doc.toObject(GroupModel::class.java)
//                    for (i in groupModel.users_list?.indices!!)
//                    {
//                        if (groupModel.users_list!![i].user_id==user_id)
//                        {
//                            group_list.add(groupModel)
//                        }
//                    }
//                }
//                if (group_list.size > 0) {
//                    onGroupsLoadListener.onTaskSuccess(group_list)
//                } else {
//                    onGroupsLoadListener.onTaskEmpty()
//                }
//            })
//    }


    // filter the group of current user
    fun getGroups(user_id: String?) {
        groupResponse.postValue(APIResponse.Loading)
        DatabaseAddresses.getGroupsRefrence()
            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
                if (e != null) {
                    Log.i("TAGTAGTAG", "Listen failed.", e)

                    groupResponse.postValue(APIResponse.onTaskError(e.localizedMessage))
                    return@EventListener
                }
                val group_list: ArrayList<GroupModel> = ArrayList<GroupModel>()
                val myGroup_list: ArrayList<String> = ArrayList<String>()

                Log.i("TAGTAGTAG", "getGroups: " + group_list)
                for (doc in value!!) {
                    val groupModel: GroupModel = doc.toObject(GroupModel::class.java)
                    for (i in groupModel.users_list?.indices!!) {
                        if (groupModel.users_list!![i].user_id == user_id) {
                            group_list.add(groupModel)
                            myGroup_list.add(groupModel.group_id.toString())
                        }
                    }
                }
                if (group_list.size > 0) {
                    groupResponse.postValue(
                        APIResponse.onTaskSuccess(
                            group_list = group_list,
                            user_list = null,
                            chat_list = null,
                            myGroupList = myGroup_list
                        )
                    )
                } else {
                    groupResponse.postValue(APIResponse.onTaskEmpty)
                }
            })
    }


    fun getSingleGroupDetail(groupId: String?, onGetUserDataListener: OnGetGroupDataListener) {
        groupId?.let {
            DatabaseAddresses.getSingleGroupsRefrence(it)
                .get()
                .addOnSuccessListener{ snapshot ->

                    if (snapshot != null && snapshot.exists()) {
                        val groupModel: GroupModel = snapshot.toObject(GroupModel::class.java)!!
                        onGetUserDataListener.onTaskSuccess(groupModel)
                        Log.d("TAG", "Current data: ${snapshot.data}")
                    } else {
                        onGetUserDataListener.onTaskEmpty()
                        Log.d("TAG", "Current data: null")
                    }
                }.addOnFailureListener { e ->
                    onGetUserDataListener.onTaskError(e.message)
                }
        }
    }


    fun getSingleGroupDetails(groupId: String?, onGetUserDataListener: OnGetGroupDataListener) {
        groupId?.let {
            DatabaseAddresses.getSingleGroupsRefrence(it)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val groupModel: GroupModel = it.toObject(GroupModel::class.java)!!
                        onGetUserDataListener.onTaskSuccess(groupModel)
                        Log.d("TAG", "Current data: ${it.data}")

                    } else {
                        onGetUserDataListener.onTaskError("error")
                    }


                }
        }
    }


    fun getChatMessages(groupId: String?, onGetChatMessages: OnGetChatMessages) {
        Log.i("TAG", "getGroupMessages:xxx simpleGroupID:$groupId")
        DatabaseAddresses.getChatMessageRefrence(groupId.toString())
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .whereEqualTo("group_id", groupId)
            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
                if (e != null) {

                    Log.i("getGroupMessages:xxx", "Listen failed.", e)
                    onGetChatMessages.onTaskError(e.localizedMessage)
                    return@EventListener
                }

                val chat_list: ArrayList<ChatMessage> = ArrayList<ChatMessage>()
                for (doc in value!!) {
                    val chatMessage: ChatMessage = doc.toObject(ChatMessage::class.java)
                    chat_list.add(chatMessage)
                    Log.i("TAG", "getGroupMessages:xxx doc:${chat_list.size}")
                }

                Log.i("TAG", "getGroupMessages:yyy doc:${chat_list.size}")

                if (chat_list.size > 0) {
                    onGetChatMessages.onTaskSuccess(chat_list)
                } else {
                    onGetChatMessages.onTaskEmpty()
                }
            })
    }

//    fun getSingleMessages(groupId: String?, userId: String, onGetChatMessages: OnGetChatMessages) {
//        Log.i("TAG", "getGroupMessages:xxx simpleGroupID:$groupId")
//        DatabaseAddresses.getChatMessageRefrence(groupId.toString())
//            .orderBy("timestamp", Query.Direction.ASCENDING)
//            .whereEqualTo("group_id", groupId)
//            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
//                if (e != null) {
//
//                    Log.i("getGroupMessages:xxx", "Listen failed.", e)
//                    onGetChatMessages.onTaskError(e.localizedMessage)
//                    return@EventListener
//                }
//
//                val chat_list: ArrayList<ChatMessage> = ArrayList<ChatMessage>()
//                for (doc in value!!) {
//                    val chatMessage: ChatMessage = doc.toObject(ChatMessage::class.java)
//                    chat_list.add(chatMessage)
//
//                }
//
//                Log.i("TAG", "getGroupMessages:yyy doc:${chat_list.size}")
//
//                if (chat_list.size > 0) {
//                    onGetChatMessages.onTaskSuccess(chat_list)
//                    // all chat receive now call to access unseen message list
//                    //   getUnseenMessages(groupId!!,userId)
//                } else {
//                    onGetChatMessages.onTaskEmpty()
//                }
//            })
//    }

    fun getOrganizationDetails(
        org_id: String?,
        onOrganizationLoadListener: OnOrganizationLoadListener
    ) {
        DatabaseAddresses.getSingleOrganizationReference(org_id)
            .get()
            .addOnSuccessListener{
                if (it.exists()) {
                    val organization: Organization = it.toObject(Organization::class.java)!!
                    onOrganizationLoadListener.onTaskSuccess(organization)
                    Log.d("TAG", "Current data: ${it.data}")
                } else {
                    onOrganizationLoadListener.onTaskEmpty()
                    Log.d("TAG", "Current data: null")
                }
            }.addOnFailureListener { e->
                onOrganizationLoadListener.onTaskError(e.message)
            }
    }

    fun getUnseenMessages(id: String, rid: String) {
        Log.d("TAG7", "getUnseenMessages :id  :$id   uid: $rid")
        chatUnSeenResponse.postValue(APIResponse.Loading)
        DatabaseAddresses.getChatMessageRefrence(id)
            .whereEqualTo("sender_id", rid)
            .whereEqualTo("message_staus", "Sent")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {

                    var chat_list: List<ChatMessage> = ArrayList<ChatMessage>()

                    chat_list = it.toObjects(ChatMessage::class.java)


                    //   Log.d("TAG7", "getUnseenMessages :list :${chat_list.toString()}")
                    if (chat_list.size > 0) {
                        chatUnSeenResponse.postValue(
                            APIResponse.onTaskSuccess(
                                group_list = null,
                                user_list = null,
                                chat_list = chat_list as ArrayList<ChatMessage>,
                                myGroupList = null
                            )
                        )
                    } else {
                        chatUnSeenResponse.postValue(APIResponse.onTaskEmpty)
                    }
                } else {
                    chatUnSeenResponse.postValue(APIResponse.onTaskEmpty)
                }
            }.addOnFailureListener {
                chatUnSeenResponse.postValue(APIResponse.onTaskError(it.localizedMessage))
            }
    }


    fun updateGroupSeenReceiverStatus(groupId: String, docId: String, rid: String) {
        val map = mapOf("msg_receivers.$rid" to "Seen")
        DatabaseAddresses.getChatMessageReference(groupId, docId)
            .update(map)
    }

    fun updateSingleSeenReceiverStatus(groupId: String, docId: String) {
        DatabaseAddresses.getChatMessageReference(groupId, docId)
            .update("message_staus", "Seen")
    }

//    fun getChatCount(userList: ) {
//        Log.v("TAG8","getChatCount")
//        userList.forEach {
//            Log.v("TAG8","id :${it.user_id}")
//            DatabaseAddresses.getChatMessageReference(groupId, docId)
//        }
//    }

    /* Note: userId for P2P chat
    * and rid for group chat is user id
     */
    fun getChatCount(
        docId: String,
        userId: String,
        type: String,
        rid: String,
        pos:Int,
        callBack: UnSeenMessageCount
    ) {
        Log.v("TAG8", "getChatCount")

        Log.v("TAG8", "id :${docId}")
        if (type == "single") {
            DatabaseAddresses.getGroupChatReference(docId)
                .whereEqualTo("message_staus", "Sent")
                .whereEqualTo("sender_id", userId)
                .get()
                .addOnSuccessListener{ value  ->
                    if (value != null && !value.isEmpty) {
                        callBack.onMessageCount(pos ,value!!.toObjects(ChatMessage::class.java))
                    } else {
                        callBack.onMessageCount(pos ,arrayListOf<ChatMessage>())
                    }
                }
        }
        else {

            Log.v("TAG8", " group id :${rid}")
            DatabaseAddresses.getGroupChatReference(docId)
                .whereEqualTo("message_staus", "Sent")
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        callBack.onMessageCount(pos , arrayListOf<ChatMessage>())
                    } else {
                        val chats = it!!.toObjects(ChatMessage::class.java)
                        val count = chats.filter { chat ->
                            chat.msg_receivers?.let { map ->
                                map.containsKey(rid) && map.getValue(rid) == "Sent"
                            }
                                ?: false
                        }

                        callBack.onMessageCount(pos ,count)
                    }

                }
        }
    }

//    fun getUserProfile(uid: String, callBack: OnGetUserDataListener) {
//        DatabaseAddresses.getSingleUserReference(uid).
//        addSnapshotListener{ snapshot , e ->
//            if (e != null) {
//
//                callBack.onTaskError(e.message)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null && snapshot.exists()) {
//                val user = snapshot.toObject(UserModel::class.java)
//                callBack.onTaskSuccess(user)
//
//            } else {
//                callBack.onTaskEmpty()
//
//            }
//        }
//    }

    fun getUserProfilee(uid: String, callBack: OnGetUserDataListener) {
        DatabaseAddresses.getSingleUserReference(uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject(UserModel::class.java)
                    callBack.onTaskSuccess(user)
                } else {
                    callBack.onTaskEmpty()

                }
            }
            .addOnFailureListener {
                callBack.onTaskError(it.localizedMessage)
            }
    }

    fun updateUserProfile(uid: String, uri: Uri, bio: String, callback: OnGetUserDataListener) {
        val filepath = FirebaseStorage.getInstance().reference?.child(
            "user/profileImages/" + uid
        )
        val uploadTask =
            filepath?.putFile(uri)?.apply {
                addOnSuccessListener {

                    filepath.downloadUrl.addOnSuccessListener { uri_ ->
                        // enter data in database
                        DatabaseAddresses.getSingleUserReference(uid)
                            .update("user_bio", bio)
                        DatabaseAddresses.getSingleUserReference(uid)
                            .update("user_image", uri_.toString())
                            .addOnSuccessListener {
                                callback.onTaskSuccess(null)
                            }
                            .addOnFailureListener {
                                callback.onTaskError(it.localizedMessage.toString())
                            }

                    }

                }
                addOnPausedListener {
                    callback.onTaskError(it.error.toString())
                }
            }


    }

    fun getSingleUser2() {

    }

}