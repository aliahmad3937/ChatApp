package com.trimad.ichat.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databinding.SelectedParticipentItemviewBinding
import com.trimad.ichat.listeners.UserRemoveListener
import com.trimad.ichat.models.UserModel

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class UsersSelectedAdapter(
    private var mList: List<UserModel>,
    val context: Context,
    val userRemoveListener: UserRemoveListener
): RecyclerView.Adapter<UserSelectedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSelectedViewHolder {
        return UserSelectedViewHolder(SelectedParticipentItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: UserSelectedViewHolder, position: Int) {
        holder.selectedParticipentItemviewBinding.selectedImageview.visibility= View.VISIBLE

        Glide.with(context)
            .load(mList[position].user_image)
            .error(R.drawable.no_image)
            .into((holder.selectedParticipentItemviewBinding.userImageview))

        holder.selectedParticipentItemviewBinding.usernameTv.text=mList[position].user_name

        holder.selectedParticipentItemviewBinding.selectedImageview.setOnClickListener {
            userRemoveListener.onUserRemove(mList[position])
        }
    }

    override fun getItemCount(): Int {
       return mList.size
    }
}

class UserSelectedViewHolder(val selectedParticipentItemviewBinding: SelectedParticipentItemviewBinding): RecyclerView.ViewHolder(selectedParticipentItemviewBinding.root)