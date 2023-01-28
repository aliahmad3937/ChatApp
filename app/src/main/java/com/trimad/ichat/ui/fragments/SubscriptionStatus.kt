package com.trimad.ichat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.R
import com.trimad.ichat.constants.ConstantsData
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.databinding.FragmentLoginBinding
import com.trimad.ichat.databinding.FragmentSubscriptionStatusBinding
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.SavedPreference
import com.trimad.ichat.utils.Utils
import gun0912.tedimagepicker.util.ToastUtil

class SubscriptionStatus : Fragment() {
    private lateinit var mBinding: FragmentSubscriptionStatusBinding
    private lateinit var mContext: MainActivity

    private var progressHUD: KProgressHUD? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding=FragmentSubscriptionStatusBinding.inflate(inflater,container,false)
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))
        progressHUD!!.setLabel("Verifying")
        progressHUD!!.show()
        Log.v("TAG85","41")

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            FirebaseFirestore.getInstance().collection(ConstantsData.users).document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnCompleteListener {
                    progressHUD!!.dismiss()
                    if(it.isSuccessful){

                        val userModel: UserModel = it.result.toObject(UserModel::class.java)!!
                        if (userModel!!.user_active == true) {
                            Log.v("TAG85","51")
                            MyApp.userModel = userModel
                            SavedPreference.setUserData(mContext,userModel)
                            mContext.updateUserDB(FirebaseAuth.getInstance().uid)
                            findNavController().navigate(R.id.action_subscriptionStatus_to_homeFragment)
                        }else{
                            Log.v("TAG85","58")
                            SavedPreference.clearPreferences(mContext)
                            mContext.updateUserDBToEmptyToken(FirebaseAuth.getInstance().uid)
                            Toast.makeText(mContext,"Admin Blocked you!", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        ToastUtil.showToast(it.exception.toString())
                    }
                }

        }





        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

}