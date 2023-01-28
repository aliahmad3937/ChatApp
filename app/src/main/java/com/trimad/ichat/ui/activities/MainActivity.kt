package com.trimad.ichat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.trimad.ichat.R
import com.trimad.ichat.databasecontroller.DatabaseUploader
import com.trimad.ichat.databasecontroller.Repoistory
import com.trimad.ichat.databinding.ActivityMainBinding
import com.trimad.ichat.listeners.OnGetUserDataListener
import com.trimad.ichat.listeners.OnUserDataSaveListener
import com.trimad.ichat.models.UserModel
import com.trimad.ichat.utils.MyApp
import com.trimad.ichat.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.kaopiz.kprogresshud.KProgressHUD
import com.trimad.ichat.databasecontroller.DatabaseAddresses
import com.trimad.ichat.models.HomeChatModel
import com.trimad.ichat.utils.SavedPreference
import gun0912.tedimagepicker.util.ToastUtil
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var mBinding: ActivityMainBinding
    private var navController: NavController? = null
    private var mAuth: FirebaseAuth? = null
    var tokenn: String? = null
    private var progressHUD: KProgressHUD? = null
     var isToastShow = false
    //  var userData:MutableLiveData<Boolean> = MutableLiveData(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        progressHUD = Utils.getProgressDialog(this@MainActivity, getString(R.string.loading))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController

        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            if (
                destination.id == R.id.loginFragment ||
                destination.id == R.id.newGroupFragment ||
                destination.id == R.id.groupDetailsFragment ||
                destination.id == R.id.addParticipentsFragment ||
                destination.id == R.id.liveGroupChatFragment ||
                destination.id == R.id.profileFragment ||
                destination.id == R.id.singleLiveChatFragment ||
                destination.id == R.id.userProfile ||
                destination.id == R.id.detailsNewGroupFragment ||
                destination.id == R.id.subscriptionStatus

            ) {
                mBinding.toolbar.visibility = View.GONE
            } else {
                mBinding.toolbar.visibility = View.VISIBLE
            }
        }

        mBinding.toolbar.title = getString(R.string.app_name)
        (this).setSupportActionBar(mBinding.toolbar)

        mAuth = FirebaseAuth.getInstance()
        getToken()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.nav_signout -> {

                val date = Date()
                val timestamp = Timestamp(date)

                DatabaseUploader.updateStatus(
                    mAuth!!.uid,
                    timestamp,
                    false,
                    object : OnUserDataSaveListener {
                        override fun onTaskSuccess() {
                            Log.i(TAG, "onTaskSuccess: ")
                            mAuth!!.signOut()
                            MyApp.userModel = null
                            SavedPreference.clearPreferences(this@MainActivity)
                            //       userData.postValue(false)
                            //   Utils.clearUserProfile(this@MainActivity)
                            startActivity(Intent(this@MainActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onTaskFailure(message: String?) {
                            Log.i(TAG, "onTaskFailure: message:$message")
                        }


                    })


                true
            }
            R.id.nav_profile -> {
                navController?.navigate(R.id.action_homeFragment_to_userProfile)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        saveStatus(false)
    }

    override fun onResume() {
        super.onResume()
        saveStatus(true)
    }

    private fun saveStatus(status: Boolean) {

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        val date = Date()
        val timestamp = Timestamp(date)

        if (mAuth.currentUser != null) {
            DatabaseUploader.updateStatus(
                mAuth.uid,
                timestamp,
                status,
                object : OnUserDataSaveListener {
                    override fun onTaskSuccess() {
                        Log.i(TAG, "onTaskSuccess: ")
                    }

                    override fun onTaskFailure(message: String?) {
                        Log.i(TAG, "onTaskFailure: message:$message")
                    }


                })

        }


    }

    fun getToken(): String? {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.i(
                        "TAG7",
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                tokenn = task.result
                Log.i("TAG7", "onCreate: token:$tokenn")
            })

        return tokenn
    }

    fun getProfile(id: String) {
        Repoistory.getUser(
            id,
            object : OnGetUserDataListener {
                override fun onTaskSuccess(userModel: UserModel?) {
                    if (userModel != null) {
                        MyApp.userModel = userModel
                        if(MyApp.userModel!!.user_active == false){
                            FirebaseAuth.getInstance().signOut()
                            MyApp.userModel = null
                            //       userData.postValue(false)
                            //   Utils.clearUserProfile(this@MainActivity)
                            startActivity(Intent(this@MainActivity, MainActivity::class.java))
                            finishAffinity()
                        }

//                        if (MyApp.userModel!!.user_active == true) {
//                            Log.v(
//                                "TAG11",
//                                "Main Activity active true"
//                            )
//                            if(FirebaseAuth.getInstance().currentUser != null)
//                            navController!!.navigate(R.id.action_global_homeFragment)
//                        }else{
//                           // ToastUtil.showToast("Admin Blocked you!")
//                            // for blocking notifications
//                            if(MyApp.userModel!!.user_token.toString().isNotEmpty()) {
//                                updateUserDBToEmptyToken(MyApp.userModel!!.user_id.toString())
//                            }
//                            if(!isToastShow){
//                                isToastShow = true
//                                Toast.makeText(this@MainActivity,"Admin Blocked you!",Toast.LENGTH_SHORT).show()
//                            }
//
//                        }

                        //   userData.postValue(true)
                    }
                    Log.v(
                        "TAG7",
                        "Main Activity get Profile id:${userModel!!.user_id}  org_id:${userModel!!.organization_id}"
                    )
//                        Toast.makeText(this@MainActivity,"Success",Toast.LENGTH_LONG)
//                            .show()

                    //       Utils.saveUserProfile(this@MainActivity,userModel)
                    //      Utils.saveUserProfile2(this@MainActivity,userModel)
                }

                override fun onTaskError(message: String?) {
                   // progressHUD!!.dismiss()
                    Toast.makeText(
                        this@MainActivity, message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.v("TAG7", "Main Activity get Profile :${message.toString()}")
                }

                override fun onTaskEmpty() {
                 //   progressHUD!!.dismiss()
                    Toast.makeText(
                        this@MainActivity, getString(R.string.no_user_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.v("TAG7", "Main Activity get Profile :onTaskEmpty")
                }

            }
        )
    }

    fun getProfile2(id: String) {
        if(!progressHUD!!.isShowing)
            progressHUD!!.show()
        Repoistory.getUser2(
            id,
            object : OnGetUserDataListener {
                override fun onTaskSuccess(userModel: UserModel?) {
                    if(progressHUD!!.isShowing)
                        progressHUD!!.dismiss()

                    if (userModel != null) {
                        MyApp.userModel = userModel
                        if (MyApp.userModel!!.user_active == true) {
                            Log.v(
                                "TAG11",
                                "Main Activity active true"
                            )
                            if(FirebaseAuth.getInstance().currentUser != null) {
                                updateUserDB(FirebaseAuth.getInstance().currentUser!!.uid)
                                getProfile(FirebaseAuth.getInstance().currentUser!!.uid)
                                navController!!.navigate(R.id.action_global_homeFragment)
                            }
                        }else{
                            // ToastUtil.showToast("Admin Blocked you!")
                            // for blocking notifications
                            if(FirebaseAuth.getInstance().currentUser != null) {
                                FirebaseAuth.getInstance().signOut()
                            }
                            if(MyApp.userModel!!.user_token.toString().isNotEmpty()) {
                                updateUserDBToEmptyToken(MyApp.userModel!!.user_id.toString())
                            }
                            if(!isToastShow){
                                isToastShow = true
                                Toast.makeText(this@MainActivity,"Admin Blocked you!",Toast.LENGTH_SHORT).show()
                            }

                        }

                        //   userData.postValue(true)
                    }
                    Log.v(
                        "TAG7",
                        "Main Activity get Profile id:${userModel!!.user_id}  org_id:${userModel!!.organization_id}"
                    )
//                        Toast.makeText(this@MainActivity,"Success",Toast.LENGTH_LONG)
//                            .show()

                    //       Utils.saveUserProfile(this@MainActivity,userModel)
                    //      Utils.saveUserProfile2(this@MainActivity,userModel)
                }

                override fun onTaskError(message: String?) {
                    if(progressHUD!!.isShowing)
                        progressHUD!!.dismiss()

                    Toast.makeText(
                        this@MainActivity, message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.v("TAG7", "Main Activity get Profile :${message.toString()}")
                }

                override fun onTaskEmpty() {
                    if(progressHUD!!.isShowing)
                        progressHUD!!.dismiss()

                    Toast.makeText(
                        this@MainActivity, getString(R.string.no_user_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.v("TAG7", "Main Activity get Profile :onTaskEmpty")
                }

            }
        )
    }



    fun updateUserDB(id: String?) {
        if(id != null) {
            val date = Date()
            val timestamp = Timestamp(date)
            if (tokenn == null) {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.i(
                                "TAG7",
                                "Fetching FCM registration token failed",
                                task.exception
                            )
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        tokenn = task.result


                        //    Utils.saveUserProfile2(mContext , userModel)
                        Log.i(
                            "TAG7",
                            "Main Activity update User: id:$id   token:${tokenn.toString()}"
                        )

                        DatabaseUploader.updateToken(
                            id,
                            tokenn.toString(),
                            timestamp,
                            true,
                        )
                        Log.i("TAG7", "onCreate: token:$tokenn")
                    })
            } else {

                //    Utils.saveUserProfile2(mContext , userModel)
                Log.i("TAG7", "Main Activity update User: id:$id   token:${tokenn.toString()}")

                DatabaseUploader.updateToken(
                    id,
                    tokenn.toString(),
                    timestamp,
                    true,
                )
            }
        }
    }

    fun updateUserDBToEmptyToken(id: String?) {
        if(id != null) {
            val date = Date()
            val timestamp = Timestamp(date)
            //    Utils.saveUserProfile2(mContext , userModel)
            Log.i("TAG7", "Main Activity update User: id:$id   token:${tokenn.toString()}")


            val docData: MutableMap<String, Any> = HashMap()
            docData["user_token"] = ""
            docData["last_seen"] = timestamp
            docData["online"] = false
            DatabaseAddresses.getSingleUserReference(id)
                .set(docData, SetOptions.merge()).addOnSuccessListener {
                    //   FirebaseAuth.getInstance().signOut()
                }
        }

    }

    fun updateChatSeenStatus(userModel: HomeChatModel, uid: String) {
              for((i,msg) in userModel.msg_count.withIndex()){
                  Log.v("TAG666","403")
                  if (msg.message_type == "group") {
                      var msg_receiver: Map<String, String>? = msg.msg_receivers
                      Log.v("TAG8", "group receive :" + msg_receiver.toString())
                      // check whether group  message is receiving by group member or not?
                      if(msg_receiver != null && msg_receiver.containsKey(uid)){
                     //     Log.v("TAG8", "group contain key msg id:"+msg_id)
                          if(msg_receiver.getValue(uid) == "Sent"){
                              Log.v("TAG8", "group contain key value Sent :")
                              Log.v("TAG666","412")
                              msg.msg_receivers!![uid] ="Seen"

                              Repoistory.updateGroupSeenReceiverStatus(
                                  groupId = msg.group_id.toString(),
                                  docId = msg.message_id.toString(),
                                  rid = uid
                              )
                          }

                      }


                      if(msg_receiver != null && msg_receiver.containsValue("Sent")){ }else{
                          // for updating status of msg over document
                          if(msg.message_staus == "Sent") {
                              Log.v("TAG666","426")
                              Repoistory.updateSingleSeenReceiverStatus(
                                  groupId = msg.group_id.toString(),
                                  docId = msg.message_id.toString()
                              )
                          }
                      }


                  }else{
                      // check whether P2P message is receiving receiver or not?
                      if(msg.sender_id.toString() != uid) {
                          Log.v("TAG666","438")
                          // if yes
                          if(msg.message_staus == "Sent") {
                              Log.v("TAG666","438")
                              Repoistory.updateSingleSeenReceiverStatus(
                                  groupId = msg.group_id.toString(),
                                  docId = msg.message_id.toString()
                              )
                          }
                      }
                  }
              }
    }

    fun updatePreferences() {
        DatabaseAddresses.getSingleUserReference(FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    var userModel: UserModel = it.toObject(UserModel::class.java)!!
                    if (userModel!!.user_active == true) {
                        MyApp.userModel = userModel
                        SavedPreference.setUserData(this,userModel)
                        updateUserDB(FirebaseAuth.getInstance().uid.toString())
                    }else{
                        FirebaseAuth.getInstance().signOut()
                        SavedPreference.clearPreferences(this)
                        updateUserDBToEmptyToken(FirebaseAuth.getInstance().uid.toString())
                        Toast.makeText(this,"Admin Blocked you!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                 //   progressHUD!!.dismiss()
                }
            }
            .addOnFailureListener {
                ToastUtil.showToast(it.localizedMessage)
           //     progressHUD!!.dismiss()
            }
    }


}