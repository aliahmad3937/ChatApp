package com.trimad.ichat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentGroupsBinding
import com.trimad.ichat.listeners.OnGroupClickListener
import com.trimad.ichat.models.APIResponse
import com.trimad.ichat.models.GroupModel
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.ui.adapters.AllGroupsAdapter
import com.trimad.ichat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.utils.MyApp

class GroupsFragment : Fragment(), OnGroupClickListener {

    private val TAG = "GroupsFragment"
    private var mBinding: FragmentGroupsBinding? = null
    private val binding get() = mBinding!!
    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null

    private var mGroupList: ArrayList<GroupModel>? = null
    private var mAdapter: AllGroupsAdapter? = null
    private lateinit var mContext:MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentGroupsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.please_wait))

        setUpRecyclerView()
        if (!MyApp.isCheckUserGroups) {
            getGroups(mAuth?.currentUser!!.uid)
        }else{
            mGroupList?.clear()
            mGroupList?.addAll(MyApp.group_list)
            mAdapter?.notifyDataSetChanged()
        }


        binding.addNewGroupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_newGroupFragment)
        }
    }

    fun getGroups(user_id: String?) {
        if (!progressHUD!!.isShowing) {
            progressHUD!!.show()
        }

        DatabaseAddresses.getGroupsRefrence()
            .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
                if (e != null) {
                    Log.i("TAGTAGTAG", "Listen failed.", e)
                    if (progressHUD!!.isShowing) {
                        progressHUD!!.dismiss()
                    }


                    return@EventListener
                }
                if (progressHUD!!.isShowing) {
                    progressHUD!!.dismiss()
                }

                MyApp.isCheckUserGroups = true

                val group_list: ArrayList<GroupModel> = ArrayList<GroupModel>()
                val myGroup_list: ArrayList<String> = ArrayList<String>()
                for (doc in value!!) {
                    val groupModel: GroupModel = doc.toObject(GroupModel::class.java)
                    for (i in groupModel.users_list?.indices!!) {
                        if (groupModel.users_list!![i].user_id == user_id) {
                            group_list.add(groupModel)
                            myGroup_list.add(groupModel.group_id.toString())
                        }
                    }
                }
                if (group_list.size > 0) {
                    MyApp.myGroup_list.clear()
                    MyApp.group_list.clear()


                    MyApp.myGroup_list = myGroup_list
                    MyApp.group_list = group_list

                    mGroupList?.clear()
                    mGroupList?.addAll(group_list)
                    mAdapter?.notifyDataSetChanged()

                } else {
                    MyApp.myGroup_list.clear()
                    MyApp.group_list.clear()

                }
            })
    }




    private fun setUpRecyclerView() {
        mGroupList = ArrayList()
        mAdapter = AllGroupsAdapter(mGroupList!!, requireContext(), mAuth!!.uid.toString() ,this)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.groupsRecyclerview.layoutManager = linearLayoutManager
        binding.groupsRecyclerview.adapter = mAdapter
    }

    override fun onGroupSelect(groupModel: GroupModel) {
        val bundle = Bundle()
        bundle.putString("group_id", groupModel.group_id)
        bundle.putString("type", "group")
        findNavController().navigate(R.id.action_homeFragment_to_liveChatFragment, bundle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }


}