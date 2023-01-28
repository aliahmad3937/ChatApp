package com.trimad.ichat.ui.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentUserProfileBinding
import com.trimad.ichat.listeners.OnGetUserDataListener
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.kaopiz.kprogresshud.KProgressHUD
import gun0912.tedimagepicker.util.ToastUtil


class UserProfile : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private var uri : Uri? = null
    private lateinit var mContext:MainActivity

    private var progressHUD: KProgressHUD? = null

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                uri = result.data!!.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri!!)
                binding.profileImage.setImageBitmap(bitmap)

              //  ToastUtil.showToast(uri.toString())
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false)
        progressHUD = Utils.getProgressDialog(requireContext(), getString(com.trimad.ichat.R.string.please_wait))
        auth = FirebaseAuth.getInstance()
        getUserProfile()
        setListeners()


        return binding.root
    }

    private fun getUserProfile(){
        Repoistory.getUserProfilee(auth.uid.toString(), object :OnGetUserDataListener{
            override fun onTaskSuccess(userModel: UserModel?) {
                     updateViews(userModel!!)
            }

            override fun onTaskError(message: String?) {

            }

            override fun onTaskEmpty() {

            }

        })


    }

    private fun updateViews(userModel: UserModel) {
        if(uri == null) {
            Glide.with(mContext)
                .load(userModel?.user_image)
                .error(R.drawable.no_image)
                .into(binding.profileImage)
        }

        binding.userNameTv.text= userModel.user_name
        binding.bioInputlayout.editText?.setText(userModel.user_bio)
        binding.emailInputlayout.editText?.setText(userModel.user_email)
        binding.organiztionInputlayout.editText?.setText(userModel.organization_id)
    }

    private fun setListeners(){


        binding.updateProfile.setOnClickListener {
            if (uri != null)
            {
                progressHUD?.show()
               Repoistory.updateUserProfile(auth.uid.toString(), uri!! , binding.bioInputlayout.editText?.text.toString() ,object :OnGetUserDataListener{
                   override fun onTaskSuccess(userModel: UserModel?) {
                       progressHUD?.dismiss()

                   }

                   override fun onTaskError(message: String?) {
                       progressHUD?.dismiss()
                   }

                   override fun onTaskEmpty() {
                       progressHUD?.dismiss()
                   }

               })

            }else{
                DatabaseAddresses.getSingleUserReference(auth.uid.toString())
                    .update("user_bio",binding.bioInputlayout.editText?.text.toString())
                ToastUtil.showToast("Bio updated Successfully!")
            }
        }


        binding.profileImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(intent)
        }

        binding.arrowBackImgview.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }







}