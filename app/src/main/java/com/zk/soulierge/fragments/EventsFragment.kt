package com.zk.soulierge.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.*
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserData
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.fragment_events.*
import okhttp3.MediaType
import okhttp3.RequestBody

class EventsFragment : BaseFragment(), OnMapReadyCallback {
    var map: GoogleMap? = null

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
        rb_event?.setOnClickListener { rb_org?.isChecked = false }
        rb_org?.setOnClickListener { rb_event?.isChecked = false }
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        callUpEventListAPI()
        val user = context?.getUserData<LoginResponse>()
        val latLng =
            LatLng(
                23.156953,
                72.6463473
            )
        if (latLng != null) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng, 18F
                )
            )
        }
    }

    private fun callUpEventListAPI(
        filtersDay: String? = null,
        selectedCategory: ArrayList<CategoryItem?> = ArrayList<CategoryItem?>()
    ) {
        context?.loadingDialog(true)
        val categories = JsonArray();
        selectedCategory?.forEach { categories.add(it?.id) }
        val json = JsonObject()
        json.add("category", categories)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getEvents(body, filter_days = filtersDay),
            singleCallback = object : SingleCallback<ArrayList<UpEventResponseItem?>> {
                override fun onSingleSuccess(o: ArrayList<UpEventResponseItem?>, message: String?) {
                    context?.loadingDialog(false)
                    o.forEach {
                        var latLng = it?.longitude?.toDouble()?.let { it1 ->
                            it?.latitude?.toDouble()?.let { it2 ->
                                LatLng(
                                    it2,
                                    it1
                                )
                            }
                        }
                        if (map != null) {
                                map?.addMarker(
                                    latLng?.let { it1 ->
                                        MarkerOptions()
                                            .position(it1).title(it?.name)
                                            .icon(BitmapDescriptorFactory.defaultMarker())
                                            .visible(true)
                                    }
                                )
                        }
                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    context?.loadingDialog(false)
                    context?.simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    context?.loadingDialog(false)
                    message?.let {
                        context?.simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }


    override fun getTagFragment(): String {
        return "events_fragment"
    }


}
