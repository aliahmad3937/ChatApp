package com.trimad.ichat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.trimad.ichat.R
import com.trimad.ichat.databinding.FragmentHomeBinding
import com.trimad.ichat.ui.activities.MainActivity
import com.trimad.ichat.ui.adapters.ChildFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {


    private val TAG = "HomeFragment"
    private lateinit var mBinding: FragmentHomeBinding

    private lateinit var mContext:MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mContext.updateUserDB(FirebaseAuth.getInstance().currentUser!!.uid)
//        mContext.getProfile(FirebaseAuth.getInstance().currentUser!!.uid)
        setUpTapBar()

    }
    private fun setUpTapBar() {
        // TabLayout
        val tabLayout = mBinding.tabs
        // ViewPager2
        val viewPager = mBinding.viewpager
        /*
            🔥 Set Adapter for ViewPager inside this fragment using this Fragment,
            more specifically childFragmentManager as param
         */
        viewPager.adapter = ChildFragmentStateAdapter(this)

        // Bind tabs and viewpager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.chats)
                1 -> tab.text = getString(R.string.groups)
                2 -> tab.text = getString(R.string.organizations)

            }
        }.attach()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }
}