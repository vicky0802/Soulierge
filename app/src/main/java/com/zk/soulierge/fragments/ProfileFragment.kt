package com.zk.soulierge.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zk.soulierge.R
import com.zk.soulierge.adapter.ProfileTabAdapter
import com.zk.soulierge.utlities.FragmentUtility
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ll_setting.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(SettingFragment())
        }
        setTabs()
    }

    private fun setTabs() {
        vp_profile.adapter = ProfileTabAdapter(context!!,childFragmentManager)

    }

    override fun getTagFragment(): String {
        return "profile_frag"
    }


}
