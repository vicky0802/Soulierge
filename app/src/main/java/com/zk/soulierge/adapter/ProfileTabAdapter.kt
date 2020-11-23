package com.zk.soulierge.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.zk.soulierge.R
import com.zk.soulierge.fragments.BaseFragment
import com.zk.soulierge.fragments.FavouritesEventFragment
import com.zk.soulierge.fragments.PastEventFragments

/**
 * Created by zeerak on 1/19/2020 bt
 */
class ProfileTabAdapter(context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var mFragments: ArrayList<BaseFragment> = ArrayList()
    private var mTitles: ArrayList<String> = ArrayList()

    init {
        mFragments.apply {
            add(PastEventFragments())
            add(FavouritesEventFragment())
        }
        mTitles.add(context.getString(R.string.past))
        mTitles.add(context.getString(R.string.favourites))
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position]
    }
}