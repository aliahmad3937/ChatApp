package com.trimad.ichat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databinding.FragmentChatHomeBinding
import com.trimad.ichat.listeners.HomeChatSelectedListener
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.ui.adapters.ChatHomeAdapter
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.models.*
import kotlin.collections.ArrayList

class ChatHomeFragment : Fragment(), HomeChatSelectedListener {

    private val TAG = "ChatHomeFragment"
    private lateinit var mBinding: FragmentChatHomeBinding
    private lateinit var mContext: MainActivity
    private val binding get() = mBinding
    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null
    private var mList: ArrayList<HomeChatModel>? = null
    private var mAdapter: ChatHomeAdapter? = null
    private var isGroupEmpty = false
    private var myGroupList: ArrayList<String>? = null
    private lateinit var uid: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding = FragmentChatHomeBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        progressHUD = Utils.getProgressDialog(requireContext(), "Loading")



        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        uid = FirebaseAuth.getInstance().uid.toString()

        if (!MyApp.isCheckUserGroups) {
            getGroups(mAuth?.currentUser!!.uid)
        } else {
            if (!MyApp.isCheckUserChats) {
                getUserChats()
            } else {
                mList!!.clear()
                mList!!.addAll(MyApp.homeChatList)
            }
        }
    }

    fun getGroups(user_id: String?) {
        if (!progressHUD!!.isShowing) {
            progressHUD!!.show()
        }

        DatabaseAddresses.getGroupsRefrence()
            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
                if (e != null) {
                    Log.i("TAGTAGTAG", "Listen failed.", e)
                    if (progressHUD!!.isShowing) {
                        progressHUD!!.dismiss()
                    }


                    return@EventListener
                }
                MyApp.isCheckUserGroups = true

                val group_list: ArrayList<GroupModel> = ArrayList<GroupModel>()
                val myGroup_list: ArrayList<String> = ArrayList<String>()
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
                    MyApp.myGroup_list.clear()
                    MyApp.group_list.clear()


                    MyApp.myGroup_list = myGroup_list
                    MyApp.group_list = group_list
                    getUserChats()
                } else {
                    getUserChats()
                    MyApp.myGroup_list.clear()
                    MyApp.group_list.clear()

                }
            })
    }


    // to get all chat of current user
    fun getUserChats() {
        if (!progressHUD!!.isShowing) {
            progressHUD!!.show()
        }


        DatabaseAddresses.getChatReference()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
                if (e != null) {
                    Log.i("TAG6", "Listen failed.", e)
                    if (progressHUD!!.isShowing) {
                        progressHUD!!.dismiss()
                    }
                    return@EventListener
                }

                if (progressHUD!!.isShowing) {
                    progressHUD!!.dismiss()
                }

                val user_list: ArrayList<HomeChatModel> = ArrayList()
                Log.i("TAG6", "getUserChats: " + value?.size())
                MyApp.isCheckUserChats = true

                for (doc in value!!) {
//                    val userModel: UserModel = doc.toObject(UserModel::class.java)
//                    user_list.add(userModel)
                    if (doc.get("type").toString() == "single") {
                        Log.i("TAG6", "getUserChats: single : 129   id:${doc.id}")

                        if (doc.get("receiver_id")
                                .toString() == uid || doc.get("sender_id").toString() == uid
                        ) {
                            val userModel = HomeChatModel(
                                user_id = if (doc.get("receiver_id")
                                        .toString() == uid
                                ) doc.get("sender_id").toString() else doc.get("receiver_id")
                                    .toString(),
                                name1 = doc.get("user_one_name").toString(),
                                name2 = doc.get("user_two_name").toString(),
                                image1 = doc.get("user_one_image").toString(),
                                image2 = doc.get("user_two_image").toString(),
                                user_bio = doc.get("message").toString(),
                                user_token = doc.get("type").toString(),
                                last_seen = doc.get("timestamp") as Timestamp,
                                doc_id = doc.id
                            )
                            user_list.add(userModel)
                        }
                    } else {
                        Log.i("TAG6", "getUserChats: group : 138   id:${doc.id}")
                        if (MyApp.myGroup_list.isNotEmpty() && MyApp.myGroup_list.contains(doc.id)) {
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
                    MyApp.homeChatList = user_list
                    mList!!.clear()
                    mList!!.addAll(user_list)
                    mAdapter!!.notifyDataSetChanged()
                    // mAdapter!!.submitList(mList!!)
                //   checkUnseenCount(user_list)
                } else {
                    //  Repoistory.chatHomeResponse.postValue(APIResponse.onTaskEmpty)
                }
            })
    }

    private fun checkUnseenCount(userList: ArrayList<HomeChatModel>) {
        for ((i, user) in userList.withIndex()) {
            if (user.user_token == "single") {
                DatabaseAddresses.getGroupChatReference(user.doc_id.toString())
                    .whereEqualTo("message_staus", "Sent")
                    .whereEqualTo("sender_id", user.user_id)
                    .get()
                    .addOnSuccessListener { value ->
                        if (value != null && !value.isEmpty) {
                            mList!![i].msg_count.clear()
                            mList!![i].msg_count = value!!.toObjects(ChatMessage::class.java)
                            mAdapter!!.notifyItemChanged(i)
                            MyApp.homeChatList.clear()
                            MyApp.homeChatList.addAll(mList!!)
                        } else {

                        }
                    }
            }
            else {

                //   Log.v("TAG8", " group id :${rid}")
                DatabaseAddresses.getGroupChatReference(user.doc_id!!)
                    .whereEqualTo("message_staus", "Sent")
                    .get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            val chats = it!!.toObjects(ChatMessage::class.java)
                            val count = chats.filter { chat ->
                                chat.msg_receivers?.let { map ->
                                    map.containsKey(uid) && map.getValue(uid) == "Sent"
                                }
                                    ?: false
                            }
                            if (count.isNotEmpty()) {
                                mList!![i].msg_count.clear()
                                mList!![i].msg_count.addAll(count)
                                mAdapter!!.notifyItemChanged(i)
                                MyApp.homeChatList.clear()
                                MyApp.homeChatList.addAll(mList!!)
                            }
                        }
                    }
            }
        }

    }


    private fun setUpRecyclerView() {
        mList = ArrayList()
        mAdapter = ChatHomeAdapter(mList!!, requireContext(), this, mAuth!!.uid.toString())
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.usersRecyclerview.layoutManager = linearLayoutManager
        binding.usersRecyclerview.setHasFixedSize(true)
        binding.usersRecyclerview.adapter = mAdapter
    }

    override fun onChatSelect(userModel: HomeChatModel) {
        mContext.updateChatSeenStatus(userModel, uid)
        if (userModel.user_token == "single") {
            val bundle = Bundle()
            bundle.putString("receiver_id", userModel.user_id)
            bundle.putString("type", "single")
            findNavController().navigate(R.id.action_homeFragment_to_singleLiveChatFragment, bundle)
        } else {
            val bundle = Bundle()
            bundle.putString("group_id", userModel.user_id)
            bundle.putString("type", "group")
            findNavController().navigate(R.id.action_homeFragment_to_liveChatFragment, bundle)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        Log.i("TAG4", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i("TAG4", "onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("TAG4", "onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("TAG4", "onDetach")
    }

    override fun onDestroy() {
        mList?.clear()
        super.onDestroy()
    }

}