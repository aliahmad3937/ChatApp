package com.trimad.ichat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentOrganizationBinding
import com.trimad.ichat.listeners.OnGetSameOrgUserListener
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.listeners.Org_UserSelecteListener
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.ui.adapters.OrganizationUsersAdapter
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import gun0912.tedimagepicker.util.ToastUtil


class OrganizationFragment : Fragment(), Org_UserSelecteListener {

    private  val TAG = "OrganizationFragment"
    private var mBinding: FragmentOrganizationBinding? = null
    private val binding get() = mBinding!!
    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null
  //  private var userModel: UserModel? = null
    private var mList: ArrayList<UserModel>? = null
    private var mAdapter: OrganizationUsersAdapter? = null
    private var mContext: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        mBinding=FragmentOrganizationBinding.inflate(inflater,container,false)

        mAuth = FirebaseAuth.getInstance()

//        userModel = Utils.getUserProfile2(requireContext())
        progressHUD = Utils.getProgressDialog(requireContext(),"Loading")

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

           loadGroupsData()


       // loadGroupsData()

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
    }

//    private fun loadGroupsData() {
//        if(mAuth == null)
//            mAuth = FirebaseAuth.getInstance()
//
//
//        progressHUD!!.show()
//        if(userModel != null && userModel!!.organization_id != null && userModel!!.organization_id != ""){
//            Log.v("TAG9","id not null")
//            Repoistory.getLiveUserByOrganization(mAuth!!.currentUser!!.uid.toString(),userModel!!.organization_id,requireContext() ,
//                object : OnGetSameOrgUserListener {
//                    override fun onTaskSuccess(userModelList: List<UserModel>) {
//                        mList?.clear()
//                        mList?.addAll(userModelList)
//                        progressHUD!!.dismiss()
//                        Log.i(TAG, "loadOrganizationUsers: totalSize:" + mList?.size)
//
//                        mAdapter?.notifyDataSetChanged()
//                    }
//
//                    override fun onTaskError(message: String?) {
//                        progressHUD!!.dismiss()
//                        Log.i(TAG, "onTaskError: exception:$message")
//
//                    }
//
//                    override fun onTaskEmpty() {
//                        progressHUD!!.dismiss()
//                        Log.i(TAG, "onTaskEmpty: ")
//                    }
//
//                })
//
//        }else{
//            Log.v("TAG9","id null ${mAuth!!.currentUser!!.uid.toString()}")
//        Repoistory.getSingleUser(mAuth!!.currentUser!!.uid.toString(),requireContext(),
//            object : OnGetSameOrgUserListener {
//                override fun onTaskSuccess(userModelList: List<UserModel>) {
//                    mList?.clear()
//                    mList?.addAll(userModelList)
//                    progressHUD!!.dismiss()
//                    Log.i(TAG, "loadOrganizationUsers: totalSize:" + mList?.size)
//
//                    mAdapter?.notifyDataSetChanged()
//                }
//
//                override fun onTaskError(message: String?) {
//                    progressHUD!!.dismiss()
//                    Log.i(TAG, "onTaskError: exception:$message")
//
//                }
//
//                override fun onTaskEmpty() {
//                    progressHUD!!.dismiss()
//                    Log.i(TAG, "onTaskEmpty: ")
//                }
//
//            })
//
//        }
//
//
//    }


    private fun getChats(){
        progressHUD!!.show()
        Repoistory.getLiveUserByOrganization(mAuth!!.currentUser!!.uid.toString(),MyApp.userModel!!.organization_id,requireContext() ,
            object : OnGetSameOrgUserListener {
                override fun onTaskSuccess(userModelList: List<UserModel>) {
                    mList?.clear()
                    mList?.addAll(userModelList)
                    progressHUD!!.dismiss()
                    Log.i(TAG, "loadOrganizationUsers: totalSize:" + mList?.size)

                    mAdapter?.notifyDataSetChanged()
                }

                override fun onTaskError(message: String?) {
                    progressHUD!!.dismiss()
                    Log.i(TAG, "onTaskError: exception:$message")

                }

                override fun onTaskEmpty() {
                    progressHUD!!.dismiss()
                    Log.i(TAG, "onTaskEmpty: ")
                }

            })
    }

    private fun loadGroupsData() {
        if(MyApp.userModel != null){
            Log.v("TAG9","id not null")
          getChats()
        }
        else{
            DatabaseAddresses.getSingleUserReference(mAuth?.currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        MyApp.userModel = it.toObject(UserModel::class.java)!!
                      getChats()
                    }
                }
                .addOnFailureListener {
                    ToastUtil.showToast("Network Error!")
                }

            Toast.makeText(mContext,"please wait...",Toast.LENGTH_SHORT).show()
        }

    }

    private fun setUpRecyclerView() {
        mList = ArrayList()
        mAdapter = OrganizationUsersAdapter(mList!!,mList!!, requireContext(),this)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.usersRecyclerview.layoutManager = linearLayoutManager
        binding.usersRecyclerview.adapter = mAdapter
    }

    override fun onUserSelect(userModel: UserModel) {
        val bundle= Bundle()
        val gson = Gson()
        val jsonStr = gson.toJson(userModel)
        bundle.putString("user_model",jsonStr)
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment,bundle)
    }

    override fun onResume() {
        super.onResume()
       // loadGroupsData()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

    override fun onDestroy() {
        mList?.clear()
        super.onDestroy()
    }

}