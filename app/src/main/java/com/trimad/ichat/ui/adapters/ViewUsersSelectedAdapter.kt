package com.trimad.ichat.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databinding.ParticipentGridItemviewBinding
import com.trimad.ichat.models.UserModel

/**
 * Created by Usman Liaqat on 22,August,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class ViewUsersSelectedAdapter(
    private var mList: List<UserModel>,
    val context: Context,
): RecyclerView.Adapter<ViewUserSelectedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewUserSelectedViewHolder {
        return ViewUserSelectedViewHolder(ParticipentGridItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewUserSelectedViewHolder, position: Int) {



        Glide.with(context)
            .load(mList[position].user_image)
            .error(R.drawable.no_image)
            .into((holder.selectedParticipentItemviewBinding.userImageview))

        holder.selectedParticipentItemviewBinding.usernameTv.text=mList[position].user_name
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
class ViewUserSelectedViewHolder(val selectedParticipentItemviewBinding: ParticipentGridItemviewBinding): RecyclerView.ViewHolder(selectedParticipentItemviewBinding.root)