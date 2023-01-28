package com.trimad.ichat.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databinding.ParticiepentsVertialItemviewBinding
import com.trimad.ichat.listeners.OnGroupMemberClick
import com.trimad.ichat.models.GroupUserModel
import gun0912.tedimagepicker.util.ToastUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class AllGroupUsersAdapter(
    private var mList: ArrayList<GroupUserModel>,
    private var tempList: ArrayList<GroupUserModel>,
    val context: Context,
    val callbacks: OnGroupMemberClick,
    val uid:String
): RecyclerView.Adapter<AllGroupUsers_ViewHolder>() {
    var myAdmin:String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllGroupUsers_ViewHolder {
        return AllGroupUsers_ViewHolder(ParticiepentsVertialItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: AllGroupUsers_ViewHolder, position: Int) {
        Glide.with(context)
            .load(mList[position].userModel?.user_image)
            .error(R.drawable.no_image)
            .into((holder.participentGridItemviewBinding.userImageview))

        holder.participentGridItemviewBinding.usernameTv.text=mList[position].userModel?.user_name
        holder.participentGridItemviewBinding.userBioTv.text=mList[position].userModel?.user_bio

        if (mList[position].isAdmin==true)
        {
            myAdmin = mList[position].userModel!!.user_id
            holder.participentGridItemviewBinding.adminTv.visibility=View.VISIBLE
        }else{
            holder.participentGridItemviewBinding.adminTv.visibility=View.GONE
        }

//        holder.itemView.setOnClickListener {
//            ToastUtil.showToast("$position click")
//        }
        holder.itemView.setOnLongClickListener {
            if(myAdmin == uid){
            callbacks.onMemberClick(position)
            }else{
                ToastUtil.showToast("Only Admin can delete group members!")
            }

            true
        }
    }

//    fun updateData(list:ArrayList<GroupUserModel>){
//        mList.clear()
//        tempList.clear()
//        mList.addAll(list)
//        tempList.addAll(list)
//
//    }

    override fun getItemCount(): Int {
        return  mList.size
    }

    fun onFilter(query: String?) {
        val charSequenceString: String = query.toString()
        mList = ArrayList()
        if (query.equals(""))
        {
            mList = tempList
        } else {
            for (model in tempList) {
                if (model.userModel!!.user_name.toString().lowercase()
                        .contains(charSequenceString.lowercase(Locale.getDefault()))
                ) {
                    (mList as ArrayList<GroupUserModel>).add(model)
                }
            }
        }
        notifyDataSetChanged()
    }
}

class AllGroupUsers_ViewHolder(val participentGridItemviewBinding: ParticiepentsVertialItemviewBinding):RecyclerView.ViewHolder(participentGridItemviewBinding.root)