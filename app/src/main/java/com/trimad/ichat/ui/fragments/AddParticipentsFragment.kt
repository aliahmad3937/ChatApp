package com.trimad.ichat.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.DatabaseUploader
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentAddParticipentsBinding
import com.trimad.ichat.listeners.OnGetSameOrgUserListener
import com.trimad.ichat.listeners.OnUserDataSaveListener
import com.trimad.ichat.listeners.UserRemoveListener
import com.trimad.ichat.listeners.UserSelectListener
import com.trimad.ichat.models.GroupMember
import com.trimad.ichat.models.GroupModel
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.models.UserSelectModel
import com.trimad.ichat.ui.adapters.UsersAdapter
import com.trimad.ichat.ui.adapters.UsersSelectedAdapter
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import java.util.stream.Collectors


class AddParticipentsFragment : Fragment(), UserSelectListener, UserRemoveListener {

    private  val TAG = "AddParticipentsFragment"
    private var mBinding: FragmentAddParticipentsBinding? = null

    private val binding get() = mBinding!!

    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null
  //  private var userModel: UserModel? = null


    private var all_users_adapter: UsersAdapter? = null
    private var selected_users_adapter: UsersSelectedAdapter? = null

    private lateinit var all_user_list: ArrayList<UserSelectModel>
    private lateinit var select_list: ArrayList<UserModel>


    private var mGroupModel:GroupModel?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding=FragmentAddParticipentsBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

        val gson = Gson()
        val jsonInString = arguments?.getString("group_model")
        mGroupModel = gson.fromJson(
            jsonInString,
            GroupModel::class.java
        )
        Log.i(TAG, "onCreateView: group_id:${mGroupModel?.group_name}")

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAuth = FirebaseAuth.getInstance()

        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))

       // val org_id= userModel!!.organization_id

        setRecyclerviews()

        loadOrganizationUsers(MyApp.userModel!!.organization_id.toString())

        binding.arrowBackImgview.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.doneBtn.setOnClickListener {

            if (select_list.size>0)
            {
                Log.i(TAG, "onViewCreated:aaa old list:"+mGroupModel?.users_list?.size)

                for (i in select_list.indices)
                {
                    val groupMember: GroupMember = GroupMember()
                    groupMember.user_id= select_list[i].user_id
                    groupMember.isAdmin=false
                    mGroupModel?.users_list?.add(groupMember)
                }

                Log.i(TAG, "onViewCreated:aaa new list:"+mGroupModel?.users_list?.size)

                progressHUD?.show()
                DatabaseUploader.saveGroup(mGroupModel!!,
                    object : OnUserDataSaveListener {
                        override fun onTaskSuccess() {
                            progressHUD?.dismiss()
                            Toast.makeText(requireContext(),getString(R.string.data_update_successfully),
                                Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        override fun onTaskFailure(message: String?) {
                            progressHUD?.dismiss()
                        }
                    })
            }else{
                Toast.makeText(requireContext(),getString(R.string.please_select_user),Toast.LENGTH_LONG).show()
            }

        }

        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                all_users_adapter!!.onFilter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                all_users_adapter!!.onFilter(newText)
                return true
            }
        })
    }

    private fun setRecyclerviews() {

        /// for top recyclerview which shows selected users
        select_list = ArrayList<UserModel>()
        val linearLayoutManager_horizontal = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.selectedContactsRecyclerview.layoutManager = linearLayoutManager_horizontal
        selected_users_adapter =
            UsersSelectedAdapter(select_list, requireContext(),this)
        binding.selectedContactsRecyclerview.adapter = selected_users_adapter

        /// for below recyclerview which shows all users
        all_user_list = ArrayList<UserSelectModel>()
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.allContactsRecyclerview.layoutManager = linearLayoutManager
        all_users_adapter =
            UsersAdapter(all_user_list, all_user_list, requireContext(), this)
        binding.allContactsRecyclerview.adapter = all_users_adapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadOrganizationUsers( org_id:String) {
        Log.i(TAG, "loadOrganizationUsers: orglist:" + org_id)
        val list = ArrayList<UserModel>()
        progressHUD!!.show()

        Repoistory.getUserByOrganization(org_id,
            object : OnGetSameOrgUserListener {
                override fun onTaskSuccess(userModelList: List<UserModel>) {
                    list.addAll(userModelList)
                    progressHUD!!.dismiss()
                    Log.i(TAG, "loadOrganizationUsers: totalSize:" + list.size)

                    proceedDataToViews(list)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun proceedDataToViews(list: ArrayList<UserModel>)
    {

        Log.i(TAG, "proceedDataToViews: before_list:" + list.size)
        val new_list = ArrayList<UserModel>()
        all_user_list.clear()
        for (j in list.indices) {
            if (!mAuth!!.uid.equals(list[j].user_id)) {
                if (!new_list.contains(list[j])) {
                    new_list.add(list[j])
                }

                if (j == list.size - 1)
                {
                    val nameSet: MutableSet<String> = HashSet()
                    val employeesDistinctById: List<UserModel> = new_list.stream()
                        .filter { e -> nameSet.add(e.user_id.toString()) }
                        .collect(Collectors.toList())
                    Log.i(TAG, "proceedDataToViews: newList:" + new_list.size)
                    Log.i(TAG, "proceedDataToViews: newLatestList:" + employeesDistinctById.size)

                    for (k in employeesDistinctById.indices)
                    {
                        val userSelectModel = UserSelectModel()
                        userSelectModel.userModel = employeesDistinctById[k]

                        for (l in mGroupModel?.users_list?.indices!!)
                        {
                            if (mGroupModel?.users_list!![l].user_id== employeesDistinctById[k].user_id)
                            {
                                userSelectModel.isSelected = true
                                break
                            }
                        }

                        Log.i(TAG, "proceedDataToViews:isTrue: "+userSelectModel.isSelected)

                        if (userSelectModel.isSelected!=true)
                        {
                            all_user_list.add(userSelectModel)
                        }

                        all_users_adapter!!.notifyDataSetChanged()

                    }

                    if (all_user_list.size<=0)
                    {
                        Toast.makeText(requireContext(),"No more users found for adding in this group",Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }

                    val text=select_list.size.toString() +" "+ getString(R.string.of)+" " + all_user_list.size.toString() +" "+getString(R.string.selected)
                    binding.numberContactsSelectedTv.text=text
                }

            }


        }

    }

    override fun onUserSelect(userModel: UserSelectModel) {
        Log.i(TAG, "onUserSelect: userselect:" + userModel.isSelected)

        if (userModel.isSelected==true)
        {
            userModel.userModel?.let { select_list.add(it) }

        }else{
            select_list.remove(userModel.userModel)
        }
        updateSelectedUserRecyclerview()
    }

    override fun onUserRemove(userModel: UserModel) {
        select_list.remove(userModel)
        for (i in all_user_list.indices)
        {
            if (all_user_list[i].userModel?.user_id == userModel.user_id)
            {
                all_user_list[i].isSelected=false
                all_users_adapter?.notifyItemChanged(i)
                break
            }
        }

        updateSelectedUserRecyclerview()
    }

    private fun updateSelectedUserRecyclerview() {
        if (select_list.size>0)
        {
            binding.selectedUsersTv.visibility=View.GONE
            val text=select_list.size.toString() +" "+ getString(R.string.of)+" " + all_user_list.size.toString() +" "+getString(R.string.selected)
            binding.numberContactsSelectedTv.text=text
        }else{
            binding.selectedUsersTv.visibility=View.VISIBLE

            val text=select_list.size.toString() +" "+ getString(R.string.of)+" " + all_user_list.size.toString() +" "+getString(R.string.selected)
            binding.numberContactsSelectedTv.text=text
        }

        selected_users_adapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}