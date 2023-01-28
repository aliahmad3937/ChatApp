package com.trimad.ichat.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.ParticiepentsVertialItemviewBinding
import com.trimad.ichat.listeners.OnGroupClickListener
import com.trimad.ichat.listeners.UnSeenMessageCount
import com.trimad.ichat.models.ChatMessage
import com.trimad.ichat.models.GroupModel

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class AllGroupsAdapter(
    private var mList: List<GroupModel>,
    val context: Context,
 val rid: String,
    val onGroupClickListener: OnGroupClickListener
) : RecyclerView.Adapter<AllGroups_ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllGroups_ViewHolder {
        return AllGroups_ViewHolder(
            ParticiepentsVertialItemviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: AllGroups_ViewHolder, position: Int) {
        Repoistory.getChatCount(mList[position].group_id.toString(),"123","group", rid ,position,object :
            UnSeenMessageCount {
            override fun onMessageCount(pos: Int,count:List<ChatMessage>) {
                if(!count.isEmpty()){
                    holder.particiepentsVertialItemviewBinding.tvCount.text= count.size.toString()
                    holder.particiepentsVertialItemviewBinding.tvCount.visibility = View.VISIBLE
                }else{
                    //  holder.particiepentsVertialItemviewBinding.tvCount.text=mList[position].msg_count.toString()
                    holder.particiepentsVertialItemviewBinding.tvCount.visibility = View.INVISIBLE
                }
            }

        })




        holder.particiepentsVertialItemviewBinding.adminTv.visibility = View.GONE
        holder.particiepentsVertialItemviewBinding.selectedImageview.visibility = View.GONE
        holder.particiepentsVertialItemviewBinding.userBioTv.visibility = View.GONE


        Glide.with(context)
            .load(mList[position].group_image)
            .error(R.drawable.no_image)
            .into((holder.particiepentsVertialItemviewBinding.userImageview))

        holder.particiepentsVertialItemviewBinding.usernameTv.text = mList[position].group_name

        holder.itemView.setOnClickListener {
            onGroupClickListener.onGroupSelect(mList[position])

        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }
}

class AllGroups_ViewHolder(val particiepentsVertialItemviewBinding: ParticiepentsVertialItemviewBinding) :
    RecyclerView.ViewHolder(particiepentsVertialItemviewBinding.root)