package com.trimad.ichat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
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
import com.trimad.ichat.constants.ConstantsData.Companion.FIREBASE_NOTIFICATION_CONTENT_TYPE
import com.trimad.ichat.constants.ConstantsData.Companion.FIREBASE_NOTIFICATION_SERVER_KEY
import com.trimad.ichat.constants.ConstantsData.Companion.FIREBASE_NOTIFICATION_URL
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentLiveGroupChatBinding
import com.trimad.ichat.listeners.OnGetChatMessages
import com.trimad.ichat.listeners.OnGetGroupDataListener
import com.trimad.ichat.listeners.OnGetUserDataListener
import com.trimad.ichat.models.*
import com.trimad.ichat.singlton.ApplicationClass
import com.trimad.ichat.singlton.MySingleton
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.ui.adapters.ChatGroupMessageAdapter
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.Utils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.utils.SavedPreference
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LiveGroupChatFragment : Fragment() {

    private  val TAG = "LiveChatFragment"

    private var mBinding:FragmentLiveGroupChatBinding?=null
    private val binding get() = mBinding!!

    private var mGroupId:String?=null
    private var mType:String?=null

    private var mGroupModel:GroupModel?=null
   // private var current_userModel: UserModel? = null

    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null
    private var chat_list:ArrayList<ChatMessage>?=null
    private var mChatAdapter:ChatGroupMessageAdapter?=null
    private var mDb:FirebaseFirestore?=null

    private var users_list:ArrayList<UserModel>?=null

    private lateinit var mContext:MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding=FragmentLiveGroupChatBinding.inflate(inflater,container,false)

        mGroupId=arguments?.getString("group_id")
        mType=arguments?.getString("type")
        if(MyApp.userModel == null){
            mAuth = FirebaseAuth.getInstance()
            mContext.updatePreferences()
           MyApp.userModel = SavedPreference.getUserData(mContext)
        }



        Log.i("TAG4", "onCreateView: group_id$mGroupId-->type$mType")
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mDb=FirebaseFirestore.getInstance()
        chat_list= ArrayList()


        setUpChatRecyclerView()

     //   current_userModel = Utils.getUserProfile2(requireContext())
        progressHUD = Utils.getProgressDialog(mContext, getString(R.string.please_wait))

        // get group details and after get group all participants list details
        getGroupData()

        setListeners()
    }

    private fun setUpChatRecyclerView() {

        mChatAdapter = ChatGroupMessageAdapter( requireContext(), mAuth?.uid.toString())
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.chatmessageRecyclerView.layoutManager = linearLayoutManager
        binding.chatmessageRecyclerView.setHasFixedSize(true)
        binding.chatmessageRecyclerView.adapter = mChatAdapter

//        binding.chatmessageRecyclerView.addOnLayoutChangeListener(
//            OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//                if (bottom < oldBottom) {
//                    binding.chatmessageRecyclerView.postDelayed(Runnable {
//                        if (chat_list!!.size > 0) {
//                            binding.chatmessageRecyclerView.smoothScrollToPosition(
//                                binding.chatmessageRecyclerView.adapter?.getItemCount()!! - 1
//                            )
//                        }
//                    }, 100)
//                }
//            })
    }

    private fun getGroupData() {

        users_list= ArrayList()
        mGroupId?.let {
            progressHUD?.show()
            Repoistory.getSingleGroupDetail(mGroupId,
                object : OnGetGroupDataListener {
                    override fun onTaskSuccess(groupModel: GroupModel?) {
                        progressHUD?.dismiss()
                        mGroupModel = groupModel
                        setDataOnViews()
                        getUsersProfiles()


                    }

                    override fun onTaskError(message: String?) {
                        progressHUD?.dismiss()
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    }

                    override fun onTaskEmpty() {
                        progressHUD?.dismiss()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_data_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }
    }

    private fun getGroupMessages() {
        progressHUD?.show()
        if(chat_list == null)
            chat_list = ArrayList()

        Log.i(TAG, "getGroupMessages: groupId:$mGroupId")
        Repoistory.getChatMessages(groupId = mGroupId,
            object :OnGetChatMessages{
                override fun onTaskSuccess(chatMessageList: List<ChatMessage>)
                {
                   // chat_list?.clear()
                    progressHUD?.dismiss()
                    Log.i(TAG, "onTaskSuccess: chatMessageList:"+chatMessageList.size)
                //    chat_list?.addAll(chatMessageList!!)
                //    mChatAdapter?.notifyDataSetChanged()
                    mChatAdapter?.submitList(chatMessageList)
                    binding.chatmessageRecyclerView.postDelayed(Runnable {
                            binding.chatmessageRecyclerView.scrollToPosition(
                                binding.chatmessageRecyclerView.adapter?.getItemCount()!! - 1
                            )
                    }, 100)
//
//                    for (i in chatMessageList.indices)
//                    {
//                        val chatReadModel=ChatReadModel()
//
//                        chatReadModel.chatMessage=chatMessageList[i]
//
//                        for (j in users_list!!.indices)
//                        {
//                            if (chatMessageList[i].sender_id==users_list!![j].user_id)
//                            {
//                                chatReadModel.chat_sender_userModel=users_list!![j]
//                                chat_list?.add(chatReadModel)
//                                Log.i(TAG, "onTaskSuccess: chatMessageList chatModel:"+chat_list?.size +"-->image:"+users_list!![j].user_image)
//                                break
//                            }
//                        }
//                        mChatAdapter?.notifyDataSetChanged()
//                    }
                 //   Log.i(TAG, "onTaskSuccess: chatList:"+chat_list!!.size)

                 //   binding.chatmessageRecyclerView.scrollToPosition(chatMessageList.size - 1)

                }

                override fun onTaskError(message: String?) {
                    progressHUD?.dismiss()
                    Log.i(TAG, "onTaskError: error:$message")
                    Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show()

                }

                override fun onTaskEmpty() {
                    progressHUD?.dismiss()
//                    chat_list?.clear()
//                    mChatAdapter?.notifyDataSetChanged()

//                    Toast.makeText(mContext,getString(R.string.no_chat_found),Toast.LENGTH_SHORT).show()

                }
            })
    }

    // get list of data for all participants whoes are include in group
    private fun getUsersProfiles() {
        Log.i(TAG, "getUsersProfiles: "+mGroupModel?.users_list!!.size)

        var stringBuilder: StringBuilder =java.lang.StringBuilder()


        for (i in mGroupModel?.users_list?.indices!!)
        {
            Repoistory.getSimpleUser(mGroupModel?.users_list!![i].user_id,
                object :OnGetUserDataListener{
                    override fun onTaskSuccess(userModel: UserModel?) {
                        if (userModel != null)
                        {
                            users_list?.add(userModel)

                            if (stringBuilder.isEmpty())
                            {
                                stringBuilder.append(userModel.user_name)
                            }else{
                                stringBuilder.append(" , "+userModel.user_name)
                            }
                            binding.statusTv.text=stringBuilder.toString()

                            if(i==mGroupModel?.users_list!!.size-1)
                            {
                                getGroupMessages()
                            }

                        }
                    }

                    override fun onTaskError(message: String?) {
                        Log.i(TAG, "onTaskEmpty: user error $message:"+mGroupModel?.users_list!![i].user_id)

                    }

                    override fun onTaskEmpty() {
                        Log.i(
                            TAG,
                            "onTaskEmpty: user not found:" + mGroupModel?.users_list!![i].user_id
                        )
                    }

                })

        }

    }

    private fun setDataOnViews() {

        if (isAdded)
        {
            Glide.with(requireContext())
                .load(mGroupModel?.group_image)
                .error(R.drawable.no_image)
                .into((binding.receiverImageview))
            binding.receiverName.text=mGroupModel?.group_name
            binding.statusTv.text=""

            var isUserExist:Boolean?=null
            for (i in mGroupModel?.users_list?.indices!!)
            {
                if (mAuth?.uid == mGroupModel?.users_list!![i].user_id)
                {
                    isUserExist=true
                    break
                }
            }
            if (isUserExist==true)
            {
                binding.linearBottom.visibility=View.VISIBLE
            }
        }



    }

    private fun setListeners() {

        binding.groupNameLayout.setOnClickListener {
            val bundle=Bundle()
            bundle.putString("group_id",mGroupId)
            findNavController().navigate(R.id.action_liveChatFragment_to_groupDetailsFragment,bundle)
        }

        binding.arrowBackImgview.setOnClickListener {
            findNavController().navigate(R.id.action_liveGroupChatFragment_to_homeFragment)
        }

        binding.checkmark.setOnClickListener {
            if (mAuth?.currentUser!=null)
            {
                var message: String = binding.inputMessage.getText().toString().trim()
                if (message.isNotEmpty())
                {
                    binding.inputMessage.setText("")
                    val date = Date()
                    val timestamp = Timestamp(date)
                    message = message.replace(System.getProperty("line.separator").toRegex(), "")

                    val msg_document= mDb?.
                    collection(ConstantsData.chatMessage)?.
                    document(mGroupId.toString())?.
                    collection(ConstantsData.message)?.document()


                    val chatInfoModel=ChatGroupInfoModel(
                        chat_id = mGroupId,
                        message = message,
                        timestamp = timestamp,
                        type = ConstantsData.message_group_type,
                        user_name = mGroupModel?.group_name,
                        group_img = mGroupModel?.group_image
                    )

                    mDb?.
                    collection(ConstantsData.chatMessage)?.
                    document(mGroupId.toString())?.set(chatInfoModel, SetOptions.merge())


                    val chatMessage=ChatMessage()
                    val receiver_list=ArrayList<String>()
                    val msg_receivers: HashMap<String, String> =HashMap()

                    for (i in mGroupModel?.users_list?.indices!!)
                    {
                        if (mGroupModel?.users_list!![i].user_id != mAuth?.uid)
                        {
                            receiver_list.add(mGroupModel?.users_list!![i].user_id!!)
                            msg_receivers[mGroupModel?.users_list!![i].user_id!!] = "Sent"
                        }
                    }
                    chatMessage.message_id=msg_document?.id
                    chatMessage.message_=message

                    chatMessage.group_id=mGroupId
                    chatMessage.message_type=mType

                    chatMessage.sender_id=mAuth?.uid
                    chatMessage.sender_name=MyApp.userModel!!.user_name
                    chatMessage.sender_image=MyApp.userModel!!.user_image
                    // receiver_list
                    chatMessage.receiver_list=receiver_list
                    chatMessage.msg_receivers=msg_receivers

                    chatMessage.timestamp=timestamp
                    chatMessage.message_staus=ConstantsData.status_sent




                    msg_document?.set(chatMessage)
                        ?.addOnCompleteListener() {
                            if (it.isSuccessful)
                            {
//                                binding.inputMessage.setText("")

                                for (i in mGroupModel?.users_list?.indices!!)
                                {
                                    if (mGroupModel?.users_list!![i].isnotify==true)
                                    {
                                        if (mGroupModel?.users_list!![i].user_id != mAuth?.uid)
                                        {
                                            sendNofitfication(mGroupModel?.users_list!![i].user_id , mGroupId)
                                        }
                                    }
                                }


                            }else{
                                Log.i(TAG, "setListeners: not successfull")
                            }
                        }

                }else{
                    Toast.makeText(requireContext(),getString(R.string.please_write_message),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),getString(R.string.user_not_login),Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendNofitfication(receiver_id: String?, groupId: String?) {

        Log.i("TAG4", "sendNofitfication: receiver_id:"+receiver_id +   "groupid :$groupId")
        var recieverModel=UserModel()
        for (i in users_list?.indices!!)
        {
            if (receiver_id==users_list!![i].user_id)
            {
                recieverModel=users_list!![i]
                break
            }
        }

        val msg: String = "New Message from "+MyApp.userModel!!.user_name

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        try {
            notification.put("title", getString(R.string.app_name))
            notification.put("to", recieverModel.user_token)
            notifcationBody.put("type", ConstantsData.message_group_type)
            notifcationBody.put("message", msg)
            notifcationBody.put("receiverid", groupId)
            notifcationBody.put(
                ConstantsData.chatGroupId,
                this.mGroupId
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
            object : JsonObjectRequest(FIREBASE_NOTIFICATION_URL, notification,
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
                    params["Authorization"] = FIREBASE_NOTIFICATION_SERVER_KEY
                    params["Content-Type"] = FIREBASE_NOTIFICATION_CONTENT_TYPE
                    return params
                }
            }

        MySingleton.getInstance(ApplicationClass.appContext!!)
            ?.addToRequestQueue(jsonObjectRequest)


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

}