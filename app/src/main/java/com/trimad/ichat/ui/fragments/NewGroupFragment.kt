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
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentNewGroupBinding
import com.trimad.ichat.listeners.OnGetSameOrgUserListener
import com.trimad.ichat.listeners.UserRemoveListener
import com.trimad.ichat.listeners.UserSelectListener
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.models.UserSelectModel
import com.trimad.ichat.ui.adapters.UsersAdapter
import com.trimad.ichat.ui.adapters.UsersSelectedAdapter
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import java.util.*
import java.util.stream.Collectors


class NewGroupFragment : Fragment(), UserSelectListener,UserRemoveListener {

    private val TAG = "NewGroupFragment"
    private var mBinding: FragmentNewGroupBinding? = null

    private val binding get() = mBinding!!

    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null
   // private var userModel: UserModel? = null

    private var all_users_adapter: UsersAdapter? = null
    private var selected_users_adapter: UsersSelectedAdapter? = null

    private lateinit var all_user_list: ArrayList<UserSelectModel>
    private lateinit var select_list: ArrayList<UserModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentNewGroupBinding.inflate(inflater, container, false);

        return mBinding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
    //    userModel = Utils.getUserProfile2(requireContext())

        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))

        setRecyclerviews()

        loadOrganizationUsers(MyApp.userModel!!.organization_id)

        binding.arrowBackImgview.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.forwardBtn.setOnClickListener {
            if (select_list.size>0)
            {
                val gson = Gson()
                val jsonString = gson.toJson(select_list)

                val  bundle=Bundle()
                bundle.putString("user_list",jsonString)
                findNavController().navigate(R.id.action_newGroupFragment_to_detailsNewGroupFragment,bundle)

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
    private fun loadOrganizationUsers(org_id: String?) {
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

                if (j == list.size - 1) {
                    val nameSet: MutableSet<String> = HashSet()
                    val employeesDistinctById: List<UserModel> = new_list.stream()
                        .filter { e -> nameSet.add(e.user_id.toString()) }
                        .collect(Collectors.toList())
                    Log.i(TAG, "proceedDataToViews: newList:" + new_list.size)
                    Log.i(TAG, "proceedDataToViews: newLatestList:" + employeesDistinctById.size)

                    for (k in employeesDistinctById.indices)
                    {
                        val userSelectModel = UserSelectModel()
                        userSelectModel.isSelected = false
                        userSelectModel.userModel = employeesDistinctById[k]
                        all_user_list.add(userSelectModel)
                        all_users_adapter!!.notifyDataSetChanged()
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
        mBinding = null;
    }
}