package com.trimad.ichat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.constants.ConstantsData
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentSingleLiveChatBinding
import com.trimad.ichat.listeners.OnGetUserDataListener
import com.trimad.ichat.models.ChatInfoModel
import com.trimad.ichat.models.ChatMessage
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.singlton.ApplicationClass
import com.trimad.ichat.singlton.MySingleton
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.ui.adapters.ChatGroupMessageAdapter
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.Utils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.utils.SavedPreference
import gun0912.tedimagepicker.util.ToastUtil
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class SingleLiveChatFragment : Fragment() {

    private var mBinding: FragmentSingleLiveChatBinding? = null
    private val binding get() = mBinding!!

    private var mReceiver_id: String? = null
    private var mType: String? = null

 //   private var current_userModel: UserModel? = null
    private var receiver_userModel: UserModel? = null

    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null
    private var chat_list: ArrayList<ChatMessage>? = null
    private var mChatAdapter: ChatGroupMessageAdapter? = null
    private var mDb: FirebaseFirestore? = null

    private lateinit var context: MainActivity

    private val TAG = "SingleLiveChatFragment"
    var id: String? = null

    private lateinit var id1: String
    private lateinit var id2: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding = FragmentSingleLiveChatBinding.inflate(inflater, container, false)

        mReceiver_id = arguments?.getString("receiver_id")
        if(MyApp.userModel == null){
            context.updatePreferences()
            MyApp.userModel = SavedPreference.getUserData(context)
        }



        mType = arguments?.getString("type")
        Log.i("TAG3", "onCreateView: receiver_id$mReceiver_id-->type$mType")



        return mBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()
        chat_list = ArrayList()

        id1 = mAuth!!.uid.toString() + "_" + mReceiver_id
        id2 = mReceiver_id + "_" + mAuth!!.uid.toString()

        id = id1

        setUpChatRecyclerView()

    //    current_userModel = Utils.getUserProfile2(requireContext())
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.please_wait))

        // get receiver details and after
        getReceiverData()

        setListeners()

        DatabaseAddresses.getChatMessageRefrence(id1)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .whereEqualTo("group_id", id1)
            .addSnapshotListener { value, e ->
                if (e == null) {
                    val chatMessageList: List<ChatMessage>? = value?.toObjects(ChatMessage::class.java)
                        if(value == null || chatMessageList == null){
                            chat_list?.clear()
                            mChatAdapter?.notifyDataSetChanged()
                        }else{
                            if (chatMessageList.size > 0) {
                                id = id1
//
//                                chat_list?.clear()
//                                chat_list?.addAll(chatMessageList!!)
//                                mChatAdapter?.notifyDataSetChanged()

                                mChatAdapter?.submitList(chatMessageList)

                                binding.chatmessageRecyclerView.postDelayed(Runnable {
                                        binding.chatmessageRecyclerView.scrollToPosition(
                                            binding.chatmessageRecyclerView.adapter?.itemCount!! - 1
                                        )

                                }, 100)


                           //     Log.i(TAG, "onTaskSuccess: chatMessageList:"+chatMessageList.size)
//                            for (i in chatMessageList!!.indices)
//                            {
//                                val chatReadModel=ChatReadModel()
//
//                                chatReadModel.chatMessage=chatMessageList[i]
//
//                                if (chatMessageList[i].sender_id==mAuth?.uid)
//                                {
//                                    chatReadModel.chat_sender_userModel=receiver_userModel
//                                    chat_list?.add(chatReadModel)
//                                }else{
//                                    chatReadModel.chat_sender_userModel=MyApp.userModel!!
//                                    chat_list?.add(chatReadModel)
//                                }
//                                mChatAdapter?.notifyDataSetChanged()
//
//                            }


                            }
                        }

                    }

            }

        DatabaseAddresses.getChatMessageRefrence(id2)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .whereEqualTo("group_id", id2)
            .addSnapshotListener { value, e ->
                if (e == null) {
                    val chatMessageList: List<ChatMessage>? = value?.toObjects(ChatMessage::class.java)
                    if(value == null || chatMessageList == null){
                    //    chat_list?.clear()
                    //    mChatAdapter?.notifyDataSetChanged()
                    }else{
                        if (chatMessageList.size > 0) {
                            id = id2

//                            chat_list?.clear()
//                            chat_list?.addAll(chatMessageList!!)
                         //   mChatAdapter?.notifyDataSetChanged()
                            mChatAdapter?.submitList(chatMessageList)
                            binding.chatmessageRecyclerView.postDelayed(Runnable {
                                binding.chatmessageRecyclerView.scrollToPosition(
                                    binding.chatmessageRecyclerView.adapter?.itemCount!! - 1
                                )

                            }, 100)

                         //   Log.i(TAG, "onTaskSuccess: chatMessageList:"+chatMessageList.size)
//                            for (i in chatMessageList!!.indices)
//                            {
//                                val chatReadModel=ChatReadModel()
//
//                                chatReadModel.chatMessage=chatMessageList[i]
//
//                                if (chatMessageList[i].sender_id==mAuth?.uid)
//                                {
//                                    chatReadModel.chat_sender_userModel=receiver_userModel
//                                    chat_list?.add(chatReadModel)
//                                }else{
//                                    chatReadModel.chat_sender_userModel= MyApp.userModel ?: UserModel(user_id = mAuth?.uid)
//                                    chat_list?.add(chatReadModel)
//                                }
//                                mChatAdapter?.notifyDataSetChanged()
//
//                            }


                      //      binding.chatmessageRecyclerView.scrollToPosition(chatMessageList.size - 1)

                        }
                    }
                }
            }



//        Repoistory.chatUnSeenResponse.observe(viewLifecycleOwner) {
//            when (it) {
//                APIResponse.Loading -> {}
//                APIResponse.onTaskEmpty -> {}
//                is APIResponse.onTaskError -> {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                    Log.v("TAG7","Unseen error :${it.message}")
//                }
//                is APIResponse.onTaskSuccess -> {
//                   Log.v("TAG7","Unseen list :${it.chat_list}")
//                    val collectionReference: CollectionReference = DatabaseAddresses.getChatMessageRefrence(it!!.chat_list!![0].group_id.toString())
//                    it.chat_list!!.forEach {
//                      collectionReference
//                            .document(it.message_id.toString())
//                            .update("message_staus","Seen")
//                    }
//
//                }
//            }
//        }


    }

    private fun getUnSennMessages(id: String) {
        Repoistory.getUnseenMessages(id, mAuth!!.uid.toString())
    }

    private fun getReceiverData() {
      //  progressHUD?.show()
        Repoistory.getUser(mReceiver_id,
            object : OnGetUserDataListener {
                override fun onTaskSuccess(userModel: UserModel?) {
                 //   progressHUD?.dismiss()
                    receiver_userModel = userModel
                    setDataOnViews()

                    //  id=mReceiver_id+"_"+mAuth!!.uid.toString()

                    //  getGroupMessages(id!!)
                }

                override fun onTaskError(message: String?) {
              //      progressHUD?.dismiss()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

                override fun onTaskEmpty() {
              //      progressHUD?.dismiss()
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.no_data_found),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

            })
    }

//    private fun getGroupMessages(id2: String) {
//        Log.i(TAG, "getGroupMessages: ppp groupId:$id2")
//
//        Repoistory.getSingleMessages(groupId = id2, userId = mReceiver_id.toString(),
//            object : OnGetChatMessages {
//                override fun onTaskSuccess(chatMessageList: List<ChatMessage>) {
//                    chat_list?.clear()
//                    Log.i(TAG, "onTaskSuccess: chatMessageList:" + chatMessageList.size)
//                    for (i in chatMessageList.indices) {
//                        val chatReadModel = ChatReadModel()
//
//                        chatReadModel.chatMessage = chatMessageList[i]
//
//                        if (chatMessageList[i].sender_id == mReceiver_id) {
//                            chatReadModel.chat_sender_userModel = receiver_userModel
//                            chat_list?.add(chatReadModel)
//                        } else {
//                            chatReadModel.chat_sender_userModel = MyApp.userModel!!
//                            chat_list?.add(chatReadModel)
//                        }
//                        mChatAdapter?.notifyDataSetChanged()
//
//                    }
//
//                }
//
//                override fun onTaskError(message: String?) {
//
//                    Log.i(TAG, "onTaskError: error:$message")
//                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//
//                }
//
//                override fun onTaskEmpty() {
//                    if (isAdded) {
////                        Toast.makeText(requireContext(),getString(R.string.no_chat_found),Toast.LENGTH_SHORT).show()
//                        id = mAuth!!.uid.toString() + "_" + mReceiver_id
//                        //   repeatGroupMessages(id.toString())
//                    }
//
//                }
//
//
//            })
//    }

//    private fun repeatGroupMessages(id: String) {
//        Log.i(TAG, "getGroupMessages: rrrr groupId:$id")
//
//        Repoistory.getSingleMessages(groupId = id, userId = mReceiver_id.toString(),
//            object : OnGetChatMessages {
//                override fun onTaskSuccess(chatMessageList: List<ChatMessage>) {
//                    chat_list?.clear()
//                    Log.i(TAG, "onTaskSuccess: chatMessageList:" + chatMessageList.size)
//                    for (i in chatMessageList.indices) {
//                        val chatReadModel = ChatReadModel()
//
//                        chatReadModel.chatMessage = chatMessageList[i]
//
//                        if (chatMessageList[i].sender_id == mAuth?.uid) {
//                            chatReadModel.chat_sender_userModel = receiver_userModel
//                            chat_list?.add(chatReadModel)
//                        } else {
//                            chatReadModel.chat_sender_userModel = MyApp.userModel!!
//                            chat_list?.add(chatReadModel)
//                        }
//                        mChatAdapter?.notifyDataSetChanged()
//
//                    }
//
//
//                 //   binding.chatmessageRecyclerView.isFocusable = true
//                    binding.chatmessageRecyclerView.scrollToPosition(chatMessageList.size - 1)
//                }
//
//                override fun onTaskError(message: String?) {
//                    Log.i(TAG, "getGroupMessages: repeat onTaskError: error:$message")
//                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//
//                }
//
//                override fun onTaskEmpty() {
//                    Log.i(TAG, "getGroupMessages: repeat onTaskEmpty")
//                    chat_list!!.clear()
//                    mChatAdapter?.notifyDataSetChanged()
//                    if (isAdded) {
//                        Toast.makeText(requireContext(), "repeat", Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//
//
//            })
//    }


    private fun setDataOnViews() {
        if (isAdded) {
            Glide.with(requireContext())
                .load(receiver_userModel?.user_image)
                .error(R.drawable.no_image)
                .into((binding.receiverImageview))
            binding.receiverName.text = receiver_userModel?.user_name

            Log.i(TAG, "setDataOnViews: timeStamp:" + receiver_userModel?.last_seen)
            if (receiver_userModel?.online == true) {
                binding.statusTv.text = getString(R.string.online)
            } else {
// To use it
                val timestamp: Timestamp? = receiver_userModel?.last_seen

                val date = timestamp?.toDate()
                binding.statusTv.text = covertTimeToText(getDate(timestamp!!))
            }

        }
    }

    fun covertTimeToText(dataDate: String?): String? {
        var convertTime: String? = null
        val suffix = "ago"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            convertTime = if (second < 60) {
                if (second == 1L) {
                    "$second second $suffix"
                } else {
                    "$second seconds $suffix"
                }
            } else if (minute < 60) {
                if (minute == 1L) {
                    "$minute minute $suffix"
                } else {
                    "$minute minutes $suffix"
                }
            } else if (hour < 24) {
                if (hour == 1L) {
                    "$hour hour $suffix"
                } else {
                    "$hour hours $suffix"
                }
            } else if (day >= 7) {
                if (day >= 365) {
                    val tempYear = day / 365
                    if (tempYear == 1L) {
                        "$tempYear year $suffix"
                    } else {
                        "$tempYear years $suffix"
                    }
                } else if (day >= 30) {
                    val tempMonth = day / 30
                    if (tempMonth == 1L) {
                        (day / 30).toString() + " month " + suffix
                    } else {
                        (day / 30).toString() + " months " + suffix
                    }
                } else {
                    val tempWeek = day / 7
                    if (tempWeek == 1L) {
                        (day / 7).toString() + " week " + suffix
                    } else {
                        (day / 7).toString() + " weeks " + suffix
                    }
                }
            } else {
                if (day == 1L) {
                    "$day day $suffix"
                } else {
                    "$day days $suffix"
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("TimeAgo", e.message + "")
        }
        return convertTime
    }

    private fun getDate(timestamp: Timestamp): String? {
        val date: Date = timestamp.toDate()
        val df =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return df.format(date)
    }

    private fun setUpChatRecyclerView() {
        mChatAdapter = ChatGroupMessageAdapter( requireContext(), mAuth?.uid.toString())
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.chatmessageRecyclerView.layoutManager = linearLayoutManager
        binding.chatmessageRecyclerView.setHasFixedSize(true)
        binding.chatmessageRecyclerView.adapter = mChatAdapter

//        binding.chatmessageRecyclerView.addOnLayoutChangeListener(
//            View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//               Log.v("TAG44","476")
//
//                if (bottom < oldBottom) {
//                    Log.v("TAG44","479")
//                    binding.chatmessageRecyclerView.postDelayed(Runnable {
//                        Log.v("TAG44","481")
//                        if (chat_list!!.size > 0) {
//                            Log.v("TAG44","483")
//                            binding.chatmessageRecyclerView.smoothScrollToPosition(
//                                binding.chatmessageRecyclerView.adapter?.itemCount!! - 1
//                            )
//                        }else{
//                            Log.v("TAG44","488")
//                        }
//                    }, 100)
//                }else{
//                    Log.v("TAG44","492 bottom:$bottom  :$oldBottom")
//                }
//            })
    }

    private fun setListeners() {
        binding.arrowBackImgview.setOnClickListener {
            findNavController().navigate(R.id.action_singleLiveChatFragment_to_homeFragment)
        }

        binding.checkmark.setOnClickListener {
            if (mAuth?.currentUser != null) {
                var message: String = binding.inputMessage.text.toString().trim()
                if (message.isNotEmpty()) {
                    binding.inputMessage.setText("")
                    val date = Date()
                    val timestamp = Timestamp(date)
                    message = message.replace(System.getProperty("line.separator").toRegex(), "")

                    val chatInfoModel = ChatInfoModel(
                        chat_id = mReceiver_id,
                        message = message,
                        timestamp = timestamp,
                        type = ConstantsData.message_single_type,
                        user_one_name = MyApp.userModel!!.user_name,
                        user_two_name = receiver_userModel?.user_name,
                        user_one_image = MyApp.userModel!!.user_image,
                        user_two_image = receiver_userModel?.user_image,
                        sender_id = mAuth!!.uid.toString(),
                        receiver_id = mReceiver_id
                    )

                    mDb?.collection(ConstantsData.chatMessage)?.document(id.toString())
                        ?.set(chatInfoModel, SetOptions.merge())

                    val msg_document =
                        mDb?.collection(ConstantsData.chatMessage)?.document(id.toString())
                            ?.collection(
                                ConstantsData.message
                            )?.document()

                    val chatMessage = ChatMessage()
                    val receiver_list = ArrayList<String>()
                    receiver_list.add(mReceiver_id.toString())

                    chatMessage.message_id = msg_document?.id
                    chatMessage.message_ = message

                    chatMessage.group_id = id.toString()
                    chatMessage.message_type = mType

                    chatMessage.sender_id = mAuth?.uid
                    chatMessage.sender_name = MyApp.userModel!!.user_name
                    chatMessage.sender_image = MyApp.userModel!!.user_image
                    // receiver_list
                    chatMessage.receiver_list = receiver_list

                    chatMessage.timestamp = timestamp
                    chatMessage.message_staus = ConstantsData.status_sent

                    msg_document?.set(chatMessage)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
//                                binding.inputMessage.setText("")
                                sendNofitfication()
                            }
                        }

                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_write_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.user_not_login),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun sendNofitfication() {


        val msg: String = "New Message from " + MyApp.userModel!!.user_name

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        try {
            notification.put("title", getString(R.string.app_name))
            notification.put("to", receiver_userModel?.user_token)
            notifcationBody.put("type", ConstantsData.message_single_type)
            notifcationBody.put("message", msg)
            notifcationBody.put("receiverid", MyApp.userModel?.user_id)


            notifcationBody.put(
                ConstantsData.chatGroupId,
                mReceiver_id
            )
            notifcationBody.put(
                ConstantsData.chatSenderId,
                mAuth!!.uid
            )

            notification.put("data", notifcationBody)
        } catch (e: JSONException) {
            Log.d("notifi", "onCreate: " + e.message)
        }


        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(
                ConstantsData.FIREBASE_NOTIFICATION_URL, notification,
                Response.Listener { response ->
                    Log.i(TAG, "onResponse: responseDataNotification\n$response")
//                    saveNotificationData(recieverModel, msg, mUserModel, message)
                },
                Response.ErrorListener { error ->
                    Log.i(
                        TAG,
                        """
                      onErrorResponse: errorNotification
                      ${error.localizedMessage}
                      """.trimIndent()
                    )
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] = ConstantsData.FIREBASE_NOTIFICATION_SERVER_KEY
                    params["Content-Type"] = ConstantsData.FIREBASE_NOTIFICATION_CONTENT_TYPE
                    return params
                }
            }

        MySingleton.getInstance(ApplicationClass.appContext!!)
            ?.addToRequestQueue(jsonObjectRequest)


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as MainActivity
    }

//    override fun onDestroy() {
//        chat_list?.clear()
//        super.onDestroy()
//    }
}