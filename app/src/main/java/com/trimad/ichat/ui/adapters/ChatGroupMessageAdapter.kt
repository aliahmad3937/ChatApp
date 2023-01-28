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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FriendMessageItemViewBinding
import com.trimad.ichat.databinding.InfoMessageItemLayoutBinding
import com.trimad.ichat.databinding.UserMessageItemViewBinding
import com.trimad.ichat.models.ChatMessage
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
class ChatGroupMessageAdapter(
     var context: Context,
    private var uid: String,
) :
    ListAdapter<ChatMessage, ChatGroupMessageAdapter.ChatGroupMessageViewHolder>(DiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatGroupMessageViewHolder {

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
                return ChatGroupMessageViewHolder(binding as UserMessageItemViewBinding)
            }
            VIEW_TYPE_FRIEND_MESSAGE -> {
                binding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.friend_message_item_view,
                        parent,
                        false
                    )
                return ChatGroupMessageViewHolder(binding as FriendMessageItemViewBinding)
            }
            VIEW_TYPE_INFO_MESSAGE -> {
                binding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.info_message_item_layout,
                        parent,
                        false
                    )
                return ChatGroupMessageViewHolder(binding as InfoMessageItemLayoutBinding)
            }

            else -> {
                return throw IllegalArgumentException("Invalid type")
            }
        }

    }

    override fun onBindViewHolder(holder: ChatGroupMessageViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE -> holder.bind(item , VIEW_TYPE_USER_MESSAGE)
            VIEW_TYPE_FRIEND_MESSAGE -> holder.bind(item , VIEW_TYPE_FRIEND_MESSAGE)
            VIEW_TYPE_INFO_MESSAGE -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList.size > 0) {

            if (currentList[position].sender_id == uid) {
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


  inner class ChatGroupMessageViewHolder : RecyclerView.ViewHolder {
        var userBinding: UserMessageItemViewBinding? = null
        var friendBinding: FriendMessageItemViewBinding? = null
        var infoMessageItemLayoutBinding: InfoMessageItemLayoutBinding? = null

        constructor(itemView: UserMessageItemViewBinding) : super(itemView.root) {
            userBinding = itemView
        }

        constructor(itemView: FriendMessageItemViewBinding) : super(itemView.root) {
            friendBinding = itemView
        }

        constructor(itemView: InfoMessageItemLayoutBinding) : super(itemView.root) {
            infoMessageItemLayoutBinding = itemView

        }


        fun bind(item: ChatMessage, VIEW_TYPE: Int){
            var msg_type =item.message_type.toString()
            var msg_id =item.message_id.toString()
            var msg_receiver: Map<String, String>? = item.msg_receivers

            when (VIEW_TYPE) {
                Companion.VIEW_TYPE_USER_MESSAGE -> {

                    userBinding!!.tvMsg.text = item.message_.toString()
                    userBinding!!.tvMsg.movementMethod = LinkMovementMethod.getInstance()
                    userBinding!!.tvMsg.setLinkTextColor(Color.BLUE)


                    userBinding!!.tvTime.text = getTime(item.timestamp!!)
                    userBinding!!.tvDate.text = getDate(item.timestamp!!)

                    if ( msg_type == "single") {
                        if (item.message_staus == "Sent") {
                            userBinding!!.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_receive))
                        } else {
                            userBinding!!.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_seen))
                        }
                    }else{
                        Log.v("TAG8", "group Sent :" + msg_receiver.toString())
                        if(msg_receiver != null && msg_receiver.containsValue("Sent")){
                            Log.v("TAG8", "group contain value Sent on sender side ")
                            userBinding!!.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_receive))
                        }else{
                            Log.v("TAG8", "group not contain value Sent on sender side ")
                            userBinding!!.msgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_seen))
                            if(item.message_staus == "Sent") {
                                Repoistory.updateSingleSeenReceiverStatus(
                                    groupId = item.group_id.toString(),
                                    docId = item.message_id.toString()
                                )
                            }
                        }
                    }

                    Glide.with(context)
                        .load(MyApp.userModel!!.user_image)
                        .error(R.drawable.no_image)
                        .into(userBinding!!.ivUser)

//                if (mMessages[position].chat_sender_userModel?.user_image?.isNotEmpty() == true) {
//                    Glide.with(mContext)
//                        .load(mMessages[position].chat_sender_userModel?.user_image)
//                        .error(R.drawable.no_image)
//                        .into(userBinding.ivUser)
//                }
                }
                VIEW_TYPE_FRIEND_MESSAGE -> {
                    friendBinding!!.tvMsg.text = item.message_.toString()
                    friendBinding!!.tvMsg.movementMethod = LinkMovementMethod.getInstance()
                    friendBinding!!.tvMsg.setLinkTextColor(Color.BLUE)
                    friendBinding!!.tvTime.text = getTime(item.timestamp!!)
                    friendBinding!!.tvDate.text = getDate(item.timestamp!!)

                    if (msg_type == "group") {
                        Log.v("TAG8", "group receive :" + msg_receiver.toString())
                        // check whether group  message is receiving by group member or not?
                        if(msg_receiver != null && msg_receiver.containsKey(uid)){
                            Log.v("TAG8", "group contain key msg id:"+msg_id)
                            if(msg_receiver.getValue(uid) == "Sent"){
                                Log.v("TAG8", "group contain key value Sent :")

                                Repoistory.updateGroupSeenReceiverStatus(
                                    groupId = item.group_id.toString(),
                                    docId = item.message_id.toString(),
                                    rid = uid
                                )
                            }

                        }
                        friendBinding!!.tvName.visibility = View.VISIBLE
                        friendBinding!!.tvName.text = item.sender_name.toString()

                        if(msg_receiver != null && msg_receiver.containsValue("Sent")){ }else{
                            // for updating status of msg over document
                            if(item.message_staus == "Sent") {
                                Repoistory.updateSingleSeenReceiverStatus(
                                    groupId = item.group_id.toString(),
                                    docId = item.message_id.toString()
                                )
                            }
                        }


                    }else{
                        // check whether P2P message is receiving receiver or not?
                        if(item.sender_id.toString() != uid) {
                            // if yes
                            Repoistory.updateSingleSeenReceiverStatus(
                                groupId = item.group_id.toString(),
                                docId = item.message_id.toString()
                            )
                        }
                    }

                    Glide.with(context)
                        .load(item.sender_image)
                        .error(R.drawable.no_image)
                        .into(friendBinding!!.ivUser)



//
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

        private fun getDate(timestamp: Timestamp): String? {
            val date: Date = timestamp.toDate()
            val df =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return df.format(date)
        }

      private fun getTime(timestamp: Timestamp): String? {
          val date: Date = timestamp.toDate()
          val df =
              SimpleDateFormat("hh:mm a", Locale.getDefault())
          return df.format(date)
      }
     private fun getDateTime(timestamp: Timestamp): String? {
            val date: Date = timestamp.toDate()
            val df =
                SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            return df.format(date)
        }

    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<ChatMessage>(){
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.message_id == newItem.message_id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return  oldItem == newItem
        }

    }



}

