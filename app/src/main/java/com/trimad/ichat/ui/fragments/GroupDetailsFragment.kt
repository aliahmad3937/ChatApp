package com.trimad.ichat.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentGroupDetailsBinding
import com.trimad.ichat.listeners.OnGetGroupDataListener
import com.trimad.ichat.listeners.OnGetUserDataListener
import com.trimad.ichat.listeners.OnGroupMemberClick
import com.trimad.ichat.models.GroupModel
import com.trimad.ichat.models.GroupUserModel
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.ui.adapters.AllGroupUsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import gun0912.tedimagepicker.util.ToastUtil


class GroupDetailsFragment : Fragment() , OnGroupMemberClick {


    private  val TAG = "GroupDetailsFragment"
    private  var mBinding:FragmentGroupDetailsBinding?=null

    private val binding get() = mBinding!!
    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null

    private var mGroupID:String?=null
    private var mGroupModel:GroupModel?=null

    private var mGroupUsersList:ArrayList<GroupUserModel>?=null
    private var mAdapter:AllGroupUsersAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding=FragmentGroupDetailsBinding.inflate(inflater,container,false)
        
        mGroupID= arguments?.getString("group_id")

        Log.i(TAG, "onCreateView: group_id$mGroupID")


        
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: group_id$mGroupID")

        mAuth = FirebaseAuth.getInstance()

       setupRecyclerView()

        getGroupDetails()

        setListeners()

    }

    private fun setupRecyclerView() {
        mGroupUsersList= ArrayList()

        mAdapter= AllGroupUsersAdapter(mGroupUsersList!!,mGroupUsersList!!,requireContext() , this , mAuth!!.uid.toString())
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.participentsRecyclerview.layoutManager=linearLayoutManager
        binding.participentsRecyclerview.adapter=mAdapter
    }

    private fun setListeners() {
        binding.arrowBackImgview.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mAdapter!!.onFilter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mAdapter!!.onFilter(newText)
                return true
            }
        })

        binding.addPersonLayout.setOnClickListener {

            mAdapter?.myAdmin?.let {
                if(mAdapter?.myAdmin == mAuth!!.uid.toString()){

                    val bundle=Bundle()
                    val gson = Gson()
                    val jsonStr = gson.toJson(mGroupModel)
                    bundle.putString("group_model",jsonStr)

                    findNavController().navigate(R.id.action_groupDetailsFragment_to_addParticipentsFragment,bundle)
                }else{
                    ToastUtil.showToast("Only Admin can add group members!")
                }
            } ?: ToastUtil.showToast("please wait...")


        }
    }

    private fun getGroupDetails() {
        Repoistory.getSingleGroupDetails(mGroupID,
        object : OnGetGroupDataListener{
            override fun onTaskSuccess(groupModel: GroupModel?) {
                Log.v("TAG9","onTaskSuccess  before :${mGroupModel?.users_list?.size}")
                if(mGroupModel != null)
                    mGroupModel = null

                mGroupModel=groupModel

                Log.v("TAG9","onTaskSuccess  after :${mGroupModel?.users_list?.size}")
                progressHUD?.dismiss()
                setDataonViews()
            }

            override fun onTaskError(message: String?) {
                progressHUD?.dismiss()
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            }

            override fun onTaskEmpty() {
                progressHUD?.dismiss()
            Toast.makeText(requireContext(),getString(R.string.no_data_found),Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setDataonViews() {
        if (isAdded)
        {
            Glide.with(requireContext())
                .load(mGroupModel?.group_image)
                .error(R.drawable.no_image)
                .into((binding.groupImageview))

            binding.groupNameTv.text = mGroupModel?.group_name
            binding.groupParticipentsTv.text =
                getString(R.string.participants)+" "+  mGroupModel?.users_list?.size.toString()

            mGroupUsersList?.clear()

            Log.v("TAG9","setDataonViews  mGroupUsersList :${mGroupUsersList?.size}")
            Log.v("TAG9","setDataonViews  users_list :${ mGroupModel?.users_list?.size}")

            for (i in mGroupModel?.users_list?.indices!!)
            {
                val memberModel = mGroupModel?.users_list!![i]

                val groupUserModel=GroupUserModel()
                groupUserModel.isAdmin=memberModel.isAdmin
                Repoistory.getUser(
                    memberModel.user_id,
                    object : OnGetUserDataListener
                    {
                        override fun onTaskSuccess(userModel: UserModel?) {
                            groupUserModel.userModel=userModel
                            mGroupUsersList?.add(groupUserModel)
                            mAdapter?.notifyDataSetChanged()
                            ////
                        }
                        override fun onTaskError(message: String?) {
                            Log.i(TAG, "onTaskEmpty: error: "+message +memberModel.user_id)

                        }
                        override fun onTaskEmpty() {
                            Log.i(TAG, "onTaskEmpty: data not found: "+memberModel.user_id)

                        }

                    }
                )
            }
//            mAdapter!!.updateData(mGroupUsersList!!)
//            mAdapter!!.notifyDataSetChanged()

        }


    }

    override fun onMemberClick(pos: Int) {



        AlertDialog.Builder(context)
            .setTitle("Delete Confirmation!")
            .setMessage("Are you sure you want to delete this member?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    // Continue with delete operation
//                    val groupMember: GroupMember = GroupMember()
//                    groupMember.user_id=    mGroupModel?.users_list!![pos].user_id
//                    groupMember.isAdmin=mGroupModel?.users_list!![pos].isAdmin
//                    groupMember.isnotify=mGroupModel?.users_list!![pos].isnotify
//                    groupMember.isadded= false
                    mGroupModel?.users_list!!.removeAt(pos)
                    mGroupUsersList!!.removeAt(pos)
                  //  mAdapter = null
                  //  setupRecyclerView()
                       mAdapter?.notifyDataSetChanged()

//        val list =ArrayList<GroupMember>()
//
//        list.addAll(   mGroupModel?.users_list!!.removeAt(pos))


                    DatabaseAddresses.getSingleGroupsRefrence(mGroupID!!).
                    update("users_list",mGroupModel?.users_list)


                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()



    }

    override fun onPause() {
        super.onPause()
        mAdapter = null
        mGroupUsersList = null
        mGroupID = null
        mGroupModel = null

    }

    override fun onResume() {
        super.onResume()
     //   setupRecyclerView()
    }

}