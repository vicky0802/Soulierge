package com.zk.soulierge.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zk.soulierge.LandingActivity

import com.zk.soulierge.R
import com.zk.soulierge.utlities.ActivityNavigationUtility
import com.zk.soulierge.utlities.FragmentUtility
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ll_account_option.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(AccountSetting())
        }

        ll_privacy.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(PrivacySettingFragment())
        }

        ll_terms.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(WebviewFragment.newInstance(getString(R.string.terms_and_condition),""))
        }

        ll_help_center.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(WebviewFragment.newInstance(getString(R.string.help_center),""))
        }

        ll_privacy_policy.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(WebviewFragment.newInstance(getString(R.string.privacy_policy),""))
        }

        ll_community.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(WebviewFragment.newInstance(getString(R.string.community_guidelines),""))
        }

        ll_email_us.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(WebviewFragment.newInstance(getString(R.string.ph_email_us),""))
        }

        ll_logout.setOnClickListener {
            ActivityNavigationUtility.navigateWith(activity)
                .setClearStack()
                .navigateTo(LandingActivity::class.java)
        }
    }

    override fun getTagFragment(): String {
        return "setting_fragment"
    }


}
