package com.trimad.ichat.ui.adapters

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FriendMessageItemViewBinding
import com.trimad.ichat.databinding.InfoMessageItemLayoutBinding
import com.trimad.ichat.databinding.UserMessageItemViewBinding
import com.trimad.ichat.models.ChatReadModel
import com.trimad.ichat.utils.MyApp
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Usman Liaqat on 25,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */

/** Modified by Ali ahmad on 8,September,2022
 * update isSeen status
 */
class ChatMessageAdapter(
    private var list: ArrayList<ChatReadModel>,
    private var context: Context,
    private var uid: String,
) : RecyclerView.Adapter<ChatMessageViewHolder>() {
    private var mMessages: ArrayList<ChatReadModel> = ArrayList<ChatReadModel>()
    private val mContext: Context
    private var mUid: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding
        when (viewType) {
            VIEW_TYPE_USER_MESSAGE -> {
                binding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.user_message_item_view,
                        parent,
                        false
                    )
                return ChatMessageViewHolder(binding as UserMessageItemViewBinding)
            }
            VIEW_TYPE_FRIEND_MESSAGE -> {
                binding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.friend_message_item_view,
                        parent,
                        false
                    )
                return ChatMessageViewHolder(binding as FriendMessageItemViewBinding)
            }
            VIEW_TYPE_INFO_MESSAGE -> {
                binding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.info_message_item_layout,
                        parent,
                        false
                    )
                return ChatMessageViewHolder(binding as InfoMessageItemLayoutBinding)
            }

            else -> {
                return throw IllegalArgumentException("Invalid type")
            }
        }

    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
       var msg_type =mMessages[position].chatMessage?.message_type.toString()
       var msg_id =mMessages[position].chatMessage?.message_id.toString()
        var msg_receiver: Map<String, String>? = mMessages[position].chatMessage?.msg_receivers
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE -> {
                val userBinding: UserMessageItemViewBinding = holder.userMessageItemViewBinding!!
                userBinding.tvMsg.text = mMessages[position].chatMessage?.message_.toString()

                userBinding.tvMsg.movementMethod = LinkMovementMethod.getInstance()
                userBinding.tvMsg.setLinkTextColor(Color.BLUE)

                userBinding.tvTime.text = getDate(mMessages[position].chatMessage?.timestamp!!)

                if ( msg_type == "single") {
                    if (mMessages[position].chatMessage?.message_staus == "Sent") {
                        userBinding.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_receive))
                    } else {
                        userBinding.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_seen))
                    }
                }else{
                    Log.v("TAG8", "group Sent :" + msg_receiver.toString())
                    if(msg_receiver != null && msg_receiver.containsValue("Sent")){
                        Log.v("TAG8", "group contain value Sent on sender side ")
                        userBinding.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_receive))
                    }else{
                        Log.v("TAG8", "group not contain value Sent on sender side ")
                        userBinding.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_seen))
                        if(mMessages[position].chatMessage?.message_staus == "Sent") {
                            Repoistory.updateSingleSeenReceiverStatus(
                                groupId = mMessages[position].chatMessage?.group_id.toString(),
                                docId = mMessages[position].chatMessage?.message_id.toString()
                            )
                        }
                    }
                }

                Glide.with(mContext)
                    .load(MyApp.userModel?.user_image)
                    .error(R.drawable.no_image)
                    .into(userBinding.ivUser)

//                if (mMessages[position].chat_sender_userModel?.user_image?.isNotEmpty() == true) {
//                    Glide.with(mContext)
//                        .load(mMessages[position].chat_sender_userModel?.user_image)
//                        .error(R.drawable.no_image)
//                        .into(userBinding.ivUser)
//                }
            }
            VIEW_TYPE_FRIEND_MESSAGE -> {
                val friendBinding: FriendMessageItemViewBinding =
                    holder.friendMessageItemViewBinding!!
                friendBinding.tvMsg.text = mMessages[position].chatMessage?.message_.toString()
                friendBinding.tvMsg.movementMethod = LinkMovementMethod.getInstance()
                friendBinding.tvMsg.setLinkTextColor(Color.BLUE)
                friendBinding.tvTime.text = getDate(mMessages[position].chatMessage?.timestamp!!)

                if (msg_type == "group") {
                    Log.v("TAG8", "group receive :" + msg_receiver.toString())
                    // check whether group  message is receiving by group member or not?
                    if(msg_receiver != null && msg_receiver.containsKey(mUid)){
                        Log.v("TAG8", "group contain key msg id:"+msg_id)
                        if(msg_receiver.getValue(mUid) == "Sent"){
                            Log.v("TAG8", "group contain key value Sent :")

                            Repoistory.updateGroupSeenReceiverStatus(
                                groupId = mMessages[position].chatMessage?.group_id.toString(),
                                docId = mMessages[position].chatMessage?.message_id.toString(),
                                rid = mUid
                            )
                        }

                    }
                    friendBinding.tvName.visibility = View.VISIBLE
                    friendBinding.tvName.text = mMessages[position].chatMessage?.sender_name.toString()

                    if(msg_receiver != null && msg_receiver.containsValue("Sent")){ }else{
             // for updating status of msg over document
                        if(mMessages[position].chatMessage?.message_staus == "Sent") {
                            Repoistory.updateSingleSeenReceiverStatus(
                                groupId = mMessages[position].chatMessage?.group_id.toString(),
                                docId = mMessages[position].chatMessage?.message_id.toString()
                            )
                        }
                    }


                }else{
                    // check whether P2P message is receiving receiver or not?
                    if(mMessages[position].chatMessage?.sender_id.toString() != mUid) {
                        // if yes
                        Repoistory.updateSingleSeenReceiverStatus(
                            groupId = mMessages[position].chatMessage?.group_id.toString(),
                            docId = mMessages[position].chatMessage?.message_id.toString()
                        )
                    }
                }


                Glide.with(mContext)
                    .load(mMessages[position].chatMessage!!.sender_image)
                    .error(R.drawable.no_image)
                    .into(friendBinding.ivUser)

//                if (mMessages[position].chat_sender_userModel?.user_image?.isNotEmpty() == true) {
//
//                    Glide.with(mContext)
//                        .load(mMessages[position].chat_sender_userModel?.user_image)
//                        .error(R.drawable.no_image)
//                        .into(friendBinding.ivUser)
//                }
            }
            VIEW_TYPE_INFO_MESSAGE -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mMessages.size > 0) {

            if (mMessages[position].chatMessage!!.sender_id == uid) {
                VIEW_TYPE_USER_MESSAGE
            } else {
                VIEW_TYPE_FRIEND_MESSAGE
            }

        } else {
            VIEW_TYPE_USER_MESSAGE
        }
    }

    companion object {
        const val VIEW_TYPE_USER_MESSAGE = 0
        const val VIEW_TYPE_FRIEND_MESSAGE = 1
        const val VIEW_TYPE_INFO_MESSAGE = 2
    }

    init {
        mMessages = list
        mContext = context
        mUid = uid
    }

    private fun getDate(timestamp: Timestamp): String? {
        val date: Date = timestamp.toDate()
        val df =
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        return df.format(date)
    }
}

class ChatMessageViewHolder : RecyclerView.ViewHolder {
    var userMessageItemViewBinding: UserMessageItemViewBinding? = null
    var friendMessageItemViewBinding: FriendMessageItemViewBinding? = null
    var infoMessageItemLayoutBinding: InfoMessageItemLayoutBinding? = null

    constructor(itemView: UserMessageItemViewBinding) : super(itemView.root) {
        userMessageItemViewBinding = itemView
    }

    constructor(itemView: FriendMessageItemViewBinding) : super(itemView.root) {
        friendMessageItemViewBinding = itemView
    }

    constructor(itemView: InfoMessageItemLayoutBinding) : super(itemView.root) {
        infoMessageItemLayoutBinding = itemView

    }

}
