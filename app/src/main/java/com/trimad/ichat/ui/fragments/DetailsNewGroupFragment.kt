package com.trimad.ichat.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.trimad.ichat.R
import com.trimad.ichat.R.string.participants
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databasecontroller.DatabaseUploader
import com.trimad.ichat.databinding.FragmentDetailsNewGroupBinding
import com.trimad.ichat.firestoragecontroller.FireStorageAddresses
import com.trimad.ichat.firestoragecontroller.FireStorageUploader
import com.trimad.ichat.listeners.OnFileUploadListener
import com.trimad.ichat.listeners.OnUserDataSaveListener
import com.trimad.ichat.models.GroupMember
import com.trimad.ichat.models.GroupModel
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.ui.adapters.ViewUsersSelectedAdapter
import com.trimad.ichat.utils.Utils
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import gun0912.tedimagepicker.builder.TedImagePicker
import java.lang.reflect.Type


class DetailsNewGroupFragment : Fragment() {

    private val TAG = "AddNewGroupFragment"
    private var mBinding: FragmentDetailsNewGroupBinding? = null
    private val binding get() = mBinding!!
    private var progressHUD: KProgressHUD? = null
    private var mAuth: FirebaseAuth? = null
   // private var userModel: UserModel? = null

    private var users_list: ArrayList<UserModel>? = null

    private var image_uri:Uri?=null
    private lateinit var mGroupName:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding = FragmentDetailsNewGroupBinding.inflate(inflater, container, false)

        val user_list_string = arguments?.getString("user_list")
        val gson = Gson()
        val listOfUsersType: Type = object : TypeToken<List<UserModel?>?>() {}.getType()
        users_list = gson.fromJson(user_list_string, listOfUsersType)
        Log.i(TAG, "onCreateView: listOfUsers:" + users_list?.size)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
    //    userModel = Utils.getUserProfile2(requireContext())
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.please_wait))

        setUpRecyclerView()

        binding.participentsTv.text=getString(participants) +" "+users_list?.size

        setListeners()


    }

    private fun setListeners() {
        binding.arrowBackImgview.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.emojiImageview.setOnClickListener {
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        }
        binding.cameraImageview.setOnClickListener {
            TedImagePicker.with(requireContext())
                .start { uri ->image_uri= uri
                    binding.userImageview.setImageURI(uri)
                    binding.cameraImageview.visibility=View.GONE
                    binding.userImageview.visibility=View.VISIBLE
                }
        }
        binding.userImageview.setOnClickListener {
            TedImagePicker.with(requireContext())
                .start { uri ->image_uri= uri
                    binding.userImageview.setImageURI(uri)
                    binding.cameraImageview.visibility=View.GONE
                    binding.userImageview.visibility=View.VISIBLE

                }
        }

        binding.doneBtn.setOnClickListener {

            mGroupName=binding.subjectTv.text.toString().trim()
            if (!TextUtils.isEmpty(mGroupName))
            {

                if (image_uri!=null)
                {
                    // upload image
                    progressHUD!!.show()

                    FireStorageUploader.uploadFile(
                        FireStorageAddresses.groupStorage,
                        image_uri!!,
                        object :OnFileUploadListener{
                            override fun onFileUploaded(url: String?) {
                                AddGroup(url.toString())
                            }

                            override fun onProgress(snapshot: UploadTask.TaskSnapshot?) {
                                Log.i(TAG, "onProgress: progress")
                            }

                            override fun onFailure(e: String?) {
                                progressHUD?.dismiss()
                                Log.i(TAG, "onFailure: e"+e)
                                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_SHORT).show()
                            }

                        }
                    )

                    
                }else{
                    /// add group data
//                    progressHUD!!.show()
//                    AddGroup("")

                    Toast.makeText(requireContext(),"Select Group Image by Clicking Camera!",Toast.LENGTH_LONG).show()
                }


            }else{
                Toast.makeText(requireContext(),getString(R.string.please_add_group_name),Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun AddGroup(image_link: String) {

        val groupModel=GroupModel()

        val ref=DatabaseAddresses.getGroupsRefrence().document()
        groupModel.group_id=ref.id
        groupModel.group_image=image_link
        groupModel.group_name=binding.subjectTv.text.toString().trim()

        val group_user_list=ArrayList<GroupMember>()
        val adminGroupMember= GroupMember()
        adminGroupMember.user_id=mAuth?.uid
        adminGroupMember.isAdmin=true
        group_user_list.add(adminGroupMember)

        for (i in users_list?.indices!!)
        {
            val groupMember:GroupMember= GroupMember()
            groupMember.user_id= users_list!![i].user_id
            groupMember.isAdmin=false
            group_user_list.add(groupMember)
        }

        groupModel.users_list=group_user_list
        Log.i(TAG, "AddGroup: user_list:"+ groupModel.users_list!!.size)

        DatabaseUploader.saveGroup(groupModel,
            object : OnUserDataSaveListener{
                override fun onTaskSuccess() {
                    progressHUD?.dismiss()
                    Toast.makeText(requireContext(),getString(R.string.data_saved_successfully),Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_detailsNewGroupFragment_to_homeFragment)
                }
                override fun onTaskFailure(message: String?) {
                    progressHUD?.dismiss()
                }
            })


    }

    private fun setUpRecyclerView() {

        var adapter= users_list?.let { ViewUsersSelectedAdapter(it,requireContext()) }
        var layout_manager=GridLayoutManager(requireContext(),3)
        binding.particpentsRecyclerviwe.layoutManager=layout_manager
        binding.particpentsRecyclerviwe.adapter=adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null;
    }
}