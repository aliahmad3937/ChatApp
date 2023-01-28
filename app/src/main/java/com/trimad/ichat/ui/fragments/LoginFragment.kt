package com.trimad.ichat.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.trimad.ichat.R
import com.trimad.ichat.databinding.FragmentLoginBinding
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.SavedPreference
import gun0912.tedimagepicker.util.ToastUtil
import java.util.*


class LoginFragment : Fragment() {

    private  val TAG = "LoginFragment"
    private var mAuth: FirebaseAuth? = null
    var authStateListener: AuthStateListener? = null
    private var token: String? = null
    private var progressHUD: KProgressHUD? = null

    private var username:String?=null
    private var password:String?=null

    private lateinit var mBinding:FragmentLoginBinding
    private lateinit var mContext:MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding=FragmentLoginBinding.inflate(inflater,container,false)
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))
            progressHUD!!.setLabel(getString(R.string.autenticate_crediationals))
//

        mAuth=FirebaseAuth.getInstance()



   //     setAuthStateListener()

        mBinding.loginBtn.setOnClickListener {

            username= mBinding.usernameInputlayout.editText?.text.toString()
            password= mBinding.passwordInputlayout.editText?.text.toString()
            if (isValid() && mBinding.cbPrivacyPolicy.isChecked)
            {
                progressHUD!!.show()
                mAuth!!.signInWithEmailAndPassword(username.toString(), password.toString())
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithEmail:success")
                            progressHUD!!.dismiss()
                            findNavController().navigate(R.id.action_loginFragment_to_subscriptionStatus)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(requireContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            progressHUD!!.dismiss()
                        }
                    }
            }else{
                ToastUtil.showToast("Please accept privacy policy to proceed!")
            }
        }


        mBinding.termsCondition.setOnClickListener {
            try {
                val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://iseeutrack.com/ichat-user-terms-and-conditions/"))
                startActivity(myIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    mContext, "No application can handle this request."
                            + " Please install a webbrowser", Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }

        return mBinding.root
    }


    private fun isValid(): Boolean {
        var result = true

        if (TextUtils.isEmpty(username)) {
            mBinding.usernameInputlayout.error = getString(R.string.required)
            result = false
        }else if (TextUtils.isEmpty(password)) {
            mBinding.passwordInputlayout.error = getString(R.string.required)
            result = false
        }

            return result
    }

//    private fun setAuthStateListener() {
//
//        FirebaseMessaging.getInstance().token
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.i(
//                        TAG,
//                        "Fetching FCM registration token failed",
//                        task.exception
//                    )
//                    return@OnCompleteListener
//                }
//
//                // Get new FCM registration token
//                token = task.result
//                Log.i(TAG, "onCreate: token:$token")
//            })
//
//        val date = Date()
//        val timestamp = Timestamp(date)
//
//        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
//            progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))
//            progressHUD!!.setLabel(getString(R.string.autenticate_crediationals))
//            progressHUD!!.show()
//            if (firebaseAuth.currentUser != null)
//            {
//           val userModel=UserModel(
//               user_id =  firebaseAuth.uid,
//               user_token =    token.toString(),
//               last_seen = timestamp,
//               online = true
//           )
//                DatabaseUploader.updateToken(
//                    firebaseAuth.uid,
//                    token.toString(),
//                    timestamp,
//                    true,
//                    object : OnUserDataSaveListener {
//                        override fun onTaskSuccess() {
//                            Log.i(TAG, "onTaskSuccess: ")
//                            Utils.saveUserProfile(mContext,userModel)
//                            Utils.saveUserProfile2(mContext,userModel)
//                            progressHUD!!.dismiss()
//                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//                        }
//                        override fun onTaskFailure(message: String?) {
//                            Log.i(TAG, "onTaskFailure: message:$message")
//                        }
//                    })
//
//
//             /*   val date = Date()
//                val timestamp = Timestamp(date)
//
//                val org_list= ArrayList<String>()
//                org_list.add("asdf")
//                org_list.add("defg")
//                org_list.add("hijkl")
//
//                val userModel=UserModel(
//                    firebaseAuth.uid,
//                    "username",
//                    "bio",
//                    "test@gmail.com",
//                    "",
//                    token,
//                    true,
//                    timestamp,
//                    org_list
//                )
//
//                DatabaseUploader.saveUserData(userModel,
//                object: OnUserDataSaveListener {
//                    override fun onTaskSuccess() {
//                        Log.i(TAG, "onTaskSuccess: ")
//                        progressHUD!!.dismiss()
//                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//                    }
//                    override fun onTaskFailure(message: String?) {
//                        Log.i(TAG, "onTaskFailure: message:$message")
//                    }
//                })*/
//
//            } else {
//                progressHUD!!.dismiss()
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
//        mAuth!!.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
      //  mAuth!!.removeAuthStateListener(authStateListener!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        if(mAuth!!.currentUser != null){
            MyApp.userModel = SavedPreference.getUserData(mContext)
        findNavController().navigate(R.id.action_loginFragment_to_subscriptionStatus)
        }
    }




}