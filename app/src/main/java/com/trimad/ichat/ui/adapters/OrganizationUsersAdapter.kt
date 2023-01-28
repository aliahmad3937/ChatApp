package com.trimad.ichat.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databinding.ParticiepentsVertialItemviewBinding
import com.trimad.ichat.listeners.Org_UserSelecteListener
import com.trimad.ichat.models.UserModel
import java.util.*

/**
 * Created by Usman Liaqat on 31,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class OrganizationUsersAdapter(
    private var mList: List<UserModel>,
    private var tempList: List<UserModel>,
    val context: Context,
    val userSelectListener: Org_UserSelecteListener
): RecyclerView.Adapter<OrganizationUsersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizationUsersViewHolder {
        return OrganizationUsersViewHolder(ParticiepentsVertialItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: OrganizationUsersViewHolder, position: Int) {
        holder.particiepentsVertialItemviewBinding.adminTv.visibility= View.GONE

        Glide.with(context)
            .load(mList[position].user_image)
            .error(R.drawable.no_image)
            .into((holder.particiepentsVertialItemviewBinding.userImageview))

        holder.particiepentsVertialItemviewBinding.usernameTv.text=mList[position].user_name
        holder.particiepentsVertialItemviewBinding.userBioTv.text=mList[position].user_bio


        holder.itemView.setOnClickListener {
            userSelectListener.onUserSelect(mList[position])
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun onFilter(query: String?) {
        val charSequenceString: String = query.toString()
        mList = ArrayList()
        if (query.equals(""))
        {
            mList = tempList
        } else {
            for (model in tempList) {
                if (model.user_name.toString().lowercase()
                        .contains(charSequenceString.lowercase(Locale.getDefault()))
                ) {
                    (mList as ArrayList<UserModel>).add(model)
                }
            }
        }
        notifyDataSetChanged()
    }
}
class OrganizationUsersViewHolder(val particiepentsVertialItemviewBinding: ParticiepentsVertialItemviewBinding) : RecyclerView.ViewHolder(particiepentsVertialItemviewBinding.root)