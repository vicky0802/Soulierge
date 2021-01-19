package com.zk.soulierge.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zk.soulierge.EventDetailActivity
import com.zk.soulierge.LOCATION_PERMISSION_REQUEST
import com.zk.soulierge.OrgDetailActivity
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.*
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserData
import com.zk.soulierge.support.utilExt.hideSoftKeyboard
import com.zk.soulierge.support.utilExt.isPermissionGranted
import com.zk.soulierge.support.utilExt.logMsg
import com.zk.soulierge.support.utils.*
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_location_search.*
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.fragment_events.img_clear
import kotlinx.android.synthetic.main.fragment_events.ll_NoData
import kotlinx.android.synthetic.main.fragment_events.ll_map
import kotlinx.android.synthetic.main.fragment_events.ll_reciclerView
import kotlinx.android.synthetic.main.fragment_events.rb_event
import kotlinx.android.synthetic.main.fragment_events.rb_org
import kotlinx.android.synthetic.main.fragment_events.rv_search
import kotlinx.android.synthetic.main.fragment_events.serch_event
import kotlinx.android.synthetic.main.row_search.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList


class EventsFragment : BaseFragment(), OnMapReadyCallback, LocationListener {
    var map: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var mCurrentLocation: Location? = null
    var locationManager: LocationManager? = null

    var recyclerViewBuilder: RecyclerViewBuilder<Any>? = null

    var extraDataObj: Map<Marker, Any> = HashMap()


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
        rb_event?.setOnClickListener {
            rb_org?.isChecked = false
            callAPI()
        }
        rb_org?.setOnClickListener {
            rb_event?.isChecked = false
            callAPI()
        }
        getUserLocation()

        serch_event?.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                return if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (serch_event?.text?.toString()?.trim()?.isEmpty() == true) {
                        context?.showAppDialog(getString(R.string.warning_search))
                    } else {
                        img_clear?.visibility = View.VISIBLE
                        callSearchAPI(serch_event?.text?.toString())
                        serch_event?.clearFocus()
                        activity?.hideSoftKeyboard()
                    }
                    true
                } else false            }
        })

        img_clear?.setOnClickListener {
            clearSearch()
        }
//        serch_event?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (query?.isEmpty() == true) {
//                    context?.showAppDialog(getString(R.string.warning_search))
//                } else {
//                    callSearchAPI(query)
//                    serch_event?.clearFocus()
//                    activity?.hideSoftKeyboard()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText?.isEmpty() == true) {
//                    ll_reciclerView.visibility = View.GONE
//                    ll_map?.visibility = View.VISIBLE
//                    serch_event?.clearFocus()
//                    activity?.hideSoftKeyboard()
//                }
//                return false
//            }
//        })
    }

    private fun clearSearch() {
        serch_event.setText("")
        ll_reciclerView.visibility = View.GONE
        img_clear?.visibility = View.INVISIBLE
        ll_map?.visibility = View.VISIBLE
        serch_event?.clearFocus()
        ll_map?.requestFocus()
        activity?.hideSoftKeyboard()
    }

    fun hideKeyboard() {
        val focusedView: View? = activity?.getWindow()?.getCurrentFocus()
        if (focusedView != null && focusedView is SearchView) { // does SearchView have focus?
            serch_event.clearFocus()
            activity?.hideSoftKeyboard()
        }
    }

    private fun callSearchAPI(query: String?) {
        if (rb_event?.isChecked == true) {
            callSearchEvents(query)
        }
        if (rb_org?.isChecked == true) {
            callSearchOrganisationListAPI(query)
        }
    }


    private fun callAPI() {
        activity?.hideSoftKeyboard()
        if (rb_event?.isChecked == true) {
            callUpEventListAPI()
        }
        if (rb_org?.isChecked == true) {
            callOrganisationListAPI()
        }
    }

    private fun setupRecycleView(o: ArrayList<Any?>) {
        ll_reciclerView?.visibility = View.VISIBLE
        ll_map?.visibility = View.GONE
        if (o.isEmpty()) {
            ll_NoData?.visibility = View.VISIBLE
            rv_search?.visibility = View.GONE
        } else {
            ll_NoData?.visibility = View.GONE
            rv_search?.visibility = View.VISIBLE
        }
        recyclerViewBuilder = rv_search?.setUp(
            R.layout.row_search,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                if (item is UpEventResponseItem) {
                    context?.let {
                        Glide.with(it).load(ApiClient.BASE_IMAGE_URL + item.fileName)
                            .placeholder(R.drawable.event_smaple)
                            .into(view.row_image)
                    }
                    view?.txttitle.text = item.name
                    view?.txtDescription.text = item.description
                    view?.txtLocation.text = item.location
                    view?.setOnClickListener {
                        map?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                item?.longitude?.toDouble()?.let { it1 ->
                                    item?.latitude?.toDouble()?.let { it2 ->
                                        LatLng(
                                            it2,
                                            it1
                                        )
                                    }
                                }, 18F
                            )
                        )
                        clearSearch()
                    }
                } else if (item is OrganisationModalItem) {
                    context?.let {
                        Glide.with(it).load(ApiClient.BASE_IMAGE_URL + item.fileName)
                            .placeholder(R.drawable.event_smaple)
                            .into(view.row_image)
                    }
                    view?.txttitle.text = item.name
                    view?.txtDescription.text = item.description
                    view?.txtLocation.text = item.location
                    view?.setOnClickListener {
                        map?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                item?.longitude?.toDouble()?.let { it1 ->
                                    item?.latitude?.toDouble()?.let { it2 ->
                                        LatLng(
                                            it2,
                                            it1
                                        )
                                    }
                                }, 18F
                            )
                        )
                       clearSearch()
                    }
                }
            }
            isNestedScrollingEnabled = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true
        map?.setOnMyLocationButtonClickListener { getUserLocation(); false }
//        fb_curren_location?.visibility = View.VISIBLE
//        fb_curren_location?.setOnClickListener { getUserLocation() }
        callAPI()
        map?.setOnInfoWindowClickListener {
            context?.markerDialog(msg = it.title, btnNegativeClick = {
                val tempModal = Gson().fromJson<TempModal>(it.tag.toString(), TempModal::class.java)
                if (tempModal.isOrg == true) {
                    val intent = Intent(context, OrgDetailActivity::class.java)
                    intent.putExtra("organisationId", tempModal.id)
                    startActivityForResult(intent, 1002)
                } else {
                    var eventIntent = Intent(context, EventDetailActivity::class.java)
                    eventIntent.putExtra("eventId", tempModal.id)
                    startActivityForResult(eventIntent, 1005)
                }
            }, btnPositiveClick = {
                val gmmIntentUri =
                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + it.position.latitude + "," + it.position.longitude)
                val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                context?.startActivity(intent)
            })

        }
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


    override fun onProviderDisabled(provider: String) {

    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


    fun getUserLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionGranted(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST
                )
            )
                if (activity?.let { LocationUtils(it).checkLocationService() } == true)
                    getLastKnownLocation()
                else
                    activity?.let { LocationUtils(it).displayLocationSettingsRequest() }
        } else
            if (activity?.let { LocationUtils(it).checkLocationService() } == true)
                getLastKnownLocation()
            else
                activity?.let { LocationUtils(it).displayLocationSettingsRequest() }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        if (mFusedLocationProviderClient == null)
            mFusedLocationProviderClient =
                context?.let { LocationServices.getFusedLocationProviderClient(it) }
        mFusedLocationProviderClient?.lastLocation?.addOnCompleteListener {
            val location = it.result
            if (location == null) {
                requestNewLocation()
            } else {
                "Latitude : ${location.latitude}".logMsg("Location")
                "Longitude : ${location.longitude}".logMsg("Location")
                requestLiveLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        if (mFusedLocationProviderClient == null)
            mFusedLocationProviderClient =
                context?.let { LocationServices.getFusedLocationProviderClient(it) }
        mFusedLocationProviderClient?.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            mCurrentLocation = locationResult?.lastLocation
//            addCurrentLocationMarker(mCurrentLocation?.latitude?.let {
//                mCurrentLocation?.longitude?.let { it1 ->
//                    LatLng(
//                        it,
//                        it1
//                    )
//                }
//            })
            "Latest Latitude : ${mCurrentLocation?.latitude}".logMsg("Location")
            "Latest Longitude : ${mCurrentLocation?.longitude}".logMsg("Location")
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestLiveLocation() {
        locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            40f,
            this
        )
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
                    map?.clear()
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
                            var temp = TempModal(isOrg = false, id = it?.id)
                            map?.addMarker(
                                latLng?.let { it1 ->
                                    MarkerOptions()
                                        .position(it1).title(it?.name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_event_marker))
                                        .visible(true)
                                        .snippet(it?.description)
                                }
                            )?.tag = Gson().toJson(temp)
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


    private fun callSearchEvents(
        query: String? = null,
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
                .searchEvents(body, search_query = query),
            singleCallback = object : SingleCallback<ArrayList<UpEventResponseItem?>> {
                override fun onSingleSuccess(o: ArrayList<UpEventResponseItem?>, message: String?) {
                    context?.loadingDialog(false)
                    setupRecycleView(o as ArrayList<Any?>)
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

    private fun callOrganisationListAPI() {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getOrganisation(),
            singleCallback = object : SingleCallback<ArrayList<OrganisationModalItem?>> {
                override fun onSingleSuccess(
                    o: ArrayList<OrganisationModalItem?>,
                    message: String?
                ) {
                    context?.loadingDialog(false)
                    map?.clear()
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
                            var temp = TempModal(isOrg = true, id = it?.id)
                            map?.addMarker(
                                latLng?.let { it1 ->
                                    MarkerOptions()
                                        .position(it1).title(it?.name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_org_marker))
                                        .visible(true)
                                        .snippet(it?.description)
                                }
                            )?.tag = Gson().toJson(temp)
                        }
                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    context?.loadingDialog(false)
                    ll_NoData?.visibility = View.VISIBLE
                    context?.simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    context?.loadingDialog(false)
                    ll_NoData?.visibility = View.VISIBLE
                    message?.let {
                        context?.simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    private fun callSearchOrganisationListAPI(query: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .searchOrganisation(query),
            singleCallback = object : SingleCallback<ArrayList<OrganisationModalItem?>> {
                override fun onSingleSuccess(
                    o: ArrayList<OrganisationModalItem?>,
                    message: String?
                ) {
                    context?.loadingDialog(false)
                    setupRecycleView(o as ArrayList<Any?>)
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    context?.loadingDialog(false)
                    ll_NoData?.visibility = View.VISIBLE
                    context?.simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    context?.loadingDialog(false)
                    ll_NoData?.visibility = View.VISIBLE
                    message?.let {
                        context?.simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getUserLocation()
                }
            }
        }
    }

//    fun addCurrentLocationMarker(latLng: LatLng?) {
//        if (map != null) {
//            map?.addMarker(
//                latLng?.let { it1 ->
//                    MarkerOptions()
//                        .position(it1).title("Current Location")
//                        .icon(BitmapDescriptorFactory.defaultMarker())
//                        .visible(true)
//                }
//            )
//            map?.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    latLng, 18F
//                )
//            )
//        }
//    }

    override fun getTagFragment(): String {
        return "events_fragment"
    }

    override fun onLocationChanged(p0: Location) {
        if (p0 != null) {
            mCurrentLocation = p0
//            addCurrentLocationMarker(mCurrentLocation?.latitude?.let {
//                mCurrentLocation?.longitude?.let { it1 ->
//                    LatLng(
//                        it,
//                        it1
//                    )
//                }
//            })
        }
    }


    override fun onResume() {
        serch_event?.clearFocus()
        super.onResume()
    }

}

data class TempModal(var isOrg: Boolean? = false, var id: String?)
