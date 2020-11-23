package com.zk.soulierge.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zk.soulierge.R

/**
 * A simple [Fragment] subclass.
 */
class PastEventFragments : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pas_event_fragments, container, false)
    }

    override fun getTagFragment(): String {
        return "past_event_frag"
    }


}
