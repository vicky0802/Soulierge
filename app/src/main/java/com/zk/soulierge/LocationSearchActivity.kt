package com.zk.soulierge

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.zk.soulierge.support.utilExt.hideSoftKeyboard
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_location_search.*
import kotlinx.android.synthetic.main.row_location.view.*
import java.io.IOException


class LocationSearchActivity : AppCompatActivity(), OnMapReadyCallback {
    var googleMap: GoogleMap? = null
    var recyclerViewBuilder: RecyclerViewBuilder<Address>? = null

    var selectedLocation: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.gMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        img_back?.setOnClickListener { onBackPressed() }

        serch_event?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                return if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (serch_event?.text?.toString()?.trim()?.isEmpty() == true) {
                        showAppDialog(getString(R.string.warning_search))
                    } else {
                        searchLocation()
                        img_clear?.visibility = View.VISIBLE
//                        serch_event?.clearFocus()
                        hideSoftKeyboard()
                    }
                    true
                } else false
            }
        })

        img_clear?.setOnClickListener {
            clearSearch()
        }

        btnSelectLocation?.setOnClickListener {
            val intent = Intent()
            if (selectedLocation != null)
                intent.putExtra("address", selectedLocation)
            setResult(RESULT_OK, intent)
            onBackPressed()
        }
    }

    private fun clearSearch() {
        serch_event.setText("")
        ll_reciclerView.visibility = View.GONE
        img_clear?.visibility = View.INVISIBLE
        ll_map?.visibility = View.VISIBLE
//        serch_event?.clearFocus()
//        ll_map?.requestFocus()
        hideSoftKeyboard()
    }

    private fun setupRecycleView(o: ArrayList<Address?>) {
        ll_reciclerView?.visibility = View.VISIBLE
        ll_map?.visibility = View.GONE
        if (o.isEmpty() == true) {
            ll_NoData?.visibility = View.VISIBLE
            rv_search?.visibility = View.GONE
        } else {
            ll_NoData?.visibility = View.GONE
            rv_search?.visibility = View.VISIBLE
        }
        recyclerViewBuilder = rv_search?.setUp(
            R.layout.row_location,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                view?.txttitleLocation?.text = item.featureName
                view?.txtDescriptionLocation?.text = item.subLocality
                view?.setOnClickListener {
                    googleMap?.clear()
                    val latLng = LatLng(item.getLatitude(), item.getLongitude())
                    googleMap?.addMarker(MarkerOptions().position(latLng).title(item.featureName))
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F))
                    selectedLocation = item
                    ll_reciclerView?.visibility = View.GONE
                    ll_map?.visibility = View.VISIBLE
                    clearSearch()
                }
            }
            isNestedScrollingEnabled = false
        }
    }

    fun searchLocation() {
        val location = serch_event.text.toString()
        var addressList: List<Address>? = null
        if (location != null || location != "") {
            val geocoder = Geocoder(this)
            try {
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            addressList?.let { setupRecycleView(it as ArrayList<Address?>) }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
    }
}