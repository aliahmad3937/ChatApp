package com.trimad.ichat.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.trimad.ichat.ui.fragments.ChatHomeFragment
import com.trimad.ichat.ui.fragments.GroupsFragment
import com.trimad.ichat.ui.fragments.OrganizationFragment

class ChildFragmentStateAdapter(private val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> ChatHomeFragment()
            1 -> GroupsFragment()
            2 -> OrganizationFragment()
            else -> ChatHomeFragment()
        }
    }

}