package com.trimad.ichat.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databinding.ParticiepentsVertialItemviewBinding
import com.trimad.ichat.listeners.UserSelectListener
import com.trimad.ichat.models.UserSelectModel
import java.util.*


/**
 * Created by Usman Liaqat on 18,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class UsersAdapter(
    private var mList: List<UserSelectModel>,
    private var tempList: List<UserSelectModel>,
    val context: Context,
    val userSelectListener: UserSelectListener
):RecyclerView.Adapter<CategoriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(ParticiepentsVertialItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.particiepentsVertialItemviewBinding.adminTv.visibility=View.GONE

        Glide.with(context)
            .load(mList[position].userModel!!.user_image)
            .error(R.drawable.no_image)
            .into((holder.particiepentsVertialItemviewBinding.userImageview))

        holder.particiepentsVertialItemviewBinding.usernameTv.text=mList[position].userModel!!.user_name
        holder.particiepentsVertialItemviewBinding.userBioTv.text=mList[position].userModel!!.user_bio


        if (mList[position].isSelected==true)
        {
            holder.particiepentsVertialItemviewBinding.selectedImageview.visibility=View.VISIBLE
        }else{
            holder.particiepentsVertialItemviewBinding.selectedImageview.visibility=View.GONE

        }

        holder.itemView.setOnClickListener {

            mList[position].isSelected = mList[position].isSelected != true
            userSelectListener.onUserSelect(mList[position])
            notifyItemChanged(position)
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
                if (model.userModel!!.user_name.toString().lowercase()
                        .contains(charSequenceString.lowercase(Locale.getDefault()))
                ) {
                    (mList as ArrayList<UserSelectModel>).add(model)
                }
            }
        }
        notifyDataSetChanged()
    }
}
class CategoriesViewHolder(val particiepentsVertialItemviewBinding: ParticiepentsVertialItemviewBinding) : RecyclerView.ViewHolder(particiepentsVertialItemviewBinding.root)