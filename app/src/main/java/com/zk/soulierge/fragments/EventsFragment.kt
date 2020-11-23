package com.zk.soulierge.fragments


import android.animation.LayoutTransition
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.zk.soulierge.R
import kotlinx.android.synthetic.main.fragment_events.*

class EventsFragment : BaseFragment(), OnMapReadyCallback {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        iv_arrow.setOnClickListener {
            iv_arrow.animate().apply {
                duration = 500
                rotation(iv_arrow.rotation+180f)
            }.start()
            fl_map.visibility = if (fl_map.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }


    }

    override fun onMapReady(p0: GoogleMap?) {
    }

    override fun getTagFragment(): String {
        return "events_fragment"
    }


}
