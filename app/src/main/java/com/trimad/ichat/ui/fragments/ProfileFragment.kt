package com.trimad.ichat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.FragmentProfileBinding
import com.trimad.ichat.listeners.OnOrganizationLoadListener
import com.trimad.ichat.models.Organization
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.utils.Utils
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD

class ProfileFragment : Fragment() {
    private var mBinding: FragmentProfileBinding? = null
    private val binding get() = mBinding!!
    private var friendModel: UserModel? = null
 //   private var userModel: UserModel? = null
    private var progressHUD: KProgressHUD? = null
    private var mOrganization:Organization?=null
    private  val TAG = "ProfileFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding=FragmentProfileBinding.inflate(inflater,container,false)
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.please_wait))
        val gson = Gson()
        val jsonInString = arguments?.getString("user_model")
        friendModel = gson.fromJson(
            jsonInString,
            UserModel::class.java
        )

    //    userModel = Utils.getUserProfile2(requireContext())

        // Inflate the layout for this fragment
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       getOrganiztionData()

        binding.startChatBtn.setOnClickListener {
            if (mOrganization!=null)
            {
                val bundle = Bundle()
                bundle.putString("receiver_id", friendModel?.user_id)
                bundle.putString("type", "single")
                findNavController().navigate(R.id.action_profileFragment_to_singleLiveChatFragment, bundle)

            }
        }

        binding.arrowBackImgview.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun getOrganiztionData()
    {
        progressHUD?.show()
        Repoistory.getOrganizationDetails(friendModel?.organization_id,
        object: OnOrganizationLoadListener{
            override fun onTaskSuccess(organization: Organization?) {
               progressHUD?.dismiss()
                mOrganization=organization
                setDataOnViews()
            }

            override fun onTaskError(message: String?) {
                progressHUD!!.dismiss()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            override fun onTaskEmpty() {
                progressHUD!!.dismiss()
                Toast.makeText(requireContext(), getString(R.string.no_data_found), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setDataOnViews() {
        if (isAdded)
        {
            Glide.with(requireContext())
                .load(friendModel?.user_image)
                .error(R.drawable.no_image)
                .into((binding.groupImageview))

            binding.userNameTv.text=friendModel?.user_name
            binding.bioInputlayout.editText?.setText(friendModel?.user_bio)
            binding.emailInputlayout.editText?.setText(friendModel?.user_email)
            binding.regionInputlayout.editText?.setText(friendModel?.user_province)
            binding.positionInputlayout.editText?.setText(friendModel?.user_bussiness)
            binding.organiztionInputlayout.editText?.setText(mOrganization?.org_name)

        }

    }

}