package com.trimad.ichat.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.ParticipantsChatsBinding
import com.trimad.ichat.listeners.HomeChatSelectedListener
import com.trimad.ichat.listeners.UnSeenMessageCount
import com.trimad.ichat.models.UserModel
import com.google.firebase.Timestamp
import com.trimad.ichat.models.ChatMessage
import com.trimad.ichat.models.HomeChatModel
import com.trimad.ichat.utils.MyApp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Ali Ahmad on 9,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class ChatHomeAdapter(
    private var mList: List<HomeChatModel>,
    val context: Context,
    val chatSelectListener: HomeChatSelectedListener,
    val uid: String
) : RecyclerView.Adapter<ChatHomeAdapter.ChatHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        return ChatHomeViewHolder(
            ParticipantsChatsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) {

            chatSelectListener.onChatSelect(mList[it])
            mList[it].msg_count.clear()
        }
    }

    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {

        Repoistory.getChatCount(
            mList[position].doc_id.toString(),
            mList[position].user_id.toString(),
            mList[position].user_token.toString(),
            uid,
            position,
            object : UnSeenMessageCount {
                override fun onMessageCount(pos: Int,count: List<ChatMessage>) {
                    if (count.isEmpty()) {
                        holder.particiepentsVertialItemviewBinding.tvCount.visibility =
                            View.INVISIBLE
                        mList[pos].msg_count.clear()
                    } else {
                        //  holder.particiepentsVertialItemviewBinding.tvCount.text=mList[position].msg_count.toString()
                        holder.particiepentsVertialItemviewBinding.tvCount.text = count.size.toString()
                        holder.particiepentsVertialItemviewBinding.tvCount.visibility = View.VISIBLE
                        mList[pos].msg_count.clear()
                        mList[pos].msg_count.addAll(count)
                      //  notifyItemChanged(pos)
                    }
                }

            })




        if (mList[position].msg_count.isEmpty()) {
            holder.particiepentsVertialItemviewBinding.tvCount.visibility =
                View.INVISIBLE
        } else {
            holder.particiepentsVertialItemviewBinding.tvCount.text =
                mList[position].msg_count.size.toString()
            holder.particiepentsVertialItemviewBinding.tvCount.visibility = View.VISIBLE
        }

        if (mList[position].user_token == "single") {
            if (mList[position].name1 == null && mList[position].name2 == null) {
                DatabaseAddresses.getSingleUserReference(mList[position].user_id)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot != null && snapshot.exists()) {
                            val userModel: UserModel =
                                snapshot.toObject(UserModel::class.java)!!
                            Glide.with(context)
                                .load(userModel.user_image)
                                .error(R.drawable.no_image)
                                .into((holder.particiepentsVertialItemviewBinding.userImageview))

                            holder.particiepentsVertialItemviewBinding.usernameTv.text =
                                userModel.user_name
                        }

                    }

            } else {
                if (mList[position].name1!!.isNotEmpty() && mList[position].name2!!.isNotEmpty()) {

                    if (mList[position].name1 == MyApp.userModel!!.user_name) {
                        Glide.with(context)
                            .load(mList[position].image2)
                            .error(R.drawable.no_image)
                            .into((holder.particiepentsVertialItemviewBinding.userImageview))

                        holder.particiepentsVertialItemviewBinding.usernameTv.text =
                            mList[position].name2.toString()

                    } else {

                        Glide.with(context)
                            .load(mList[position].image1)
                            .error(R.drawable.no_image)
                            .into((holder.particiepentsVertialItemviewBinding.userImageview))

                        holder.particiepentsVertialItemviewBinding.usernameTv.text =
                            mList[position].name1.toString()
                    }

                } else {
                    DatabaseAddresses.getSingleUserReference(mList[position].user_id)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            if (snapshot != null && snapshot.exists()) {
                                val userModel: UserModel =
                                    snapshot.toObject(UserModel::class.java)!!
                                Glide.with(context)
                                    .load(userModel.user_image)
                                    .error(R.drawable.no_image)
                                    .into((holder.particiepentsVertialItemviewBinding.userImageview))

                                holder.particiepentsVertialItemviewBinding.usernameTv.text =
                                    userModel.user_name
                            }

                        }
                }
            }
        } else {
            Glide.with(context)
                .load(mList[position].user_image)
                .error(R.drawable.no_image)
                .into((holder.particiepentsVertialItemviewBinding.userImageview))

            holder.particiepentsVertialItemviewBinding.usernameTv.text = mList[position].user_name
        }


        holder.particiepentsVertialItemviewBinding.userBioTv.text = mList[position].user_bio
        val timestamp: Timestamp? = mList[position].last_seen
        holder.particiepentsVertialItemviewBinding.tvTime.text = getDate(timestamp!!)


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    fun updateItem() {

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
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        return df.format(date)
    }

    //
//    private fun getDate(timestamp: Timestamp): String? {
//        val date: Date = timestamp.toDate()
//        val df =
//            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        return df.format(date)
//    }
    class ChatHomeViewHolder(
        val particiepentsVertialItemviewBinding: ParticipantsChatsBinding,
        itemClick: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(particiepentsVertialItemviewBinding.root) {

        init {
            particiepentsVertialItemviewBinding.root.setOnClickListener {
                itemClick(adapterPosition)
            }
        }
    }

}

