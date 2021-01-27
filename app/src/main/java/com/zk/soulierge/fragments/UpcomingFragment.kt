package com.zk.soulierge.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.android.gms.location.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zk.soulierge.EventDetailActivity
import com.zk.soulierge.LOCATION_PERMISSION_REQUEST
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.*
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.*
import com.zk.soulierge.support.utils.*
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.dialog_category.view.*
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.fragment_upcoming.*
import kotlinx.android.synthetic.main.row_dialog_category.view.*
import kotlinx.android.synthetic.main.row_upcoming_event.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class UpcomingFragment : BaseFragment(), LocationListener {
    var categoryBuilder: RecyclerViewBuilder<CategoryItem>? = null
    var categoryList = ArrayList<CategoryItem?>()
    var user = context?.getUserData<LoginResponse>()

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var mCurrentLocation: Location? = null
    var locationManager: LocationManager? = null

    //    var selectedCategory = ArrayList<CategoryItem?>()
    var upEventBuilder: RecyclerViewBuilder<UpEventResponseItem>? = null

    var isNearMe: Boolean? = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun getTagFragment(): String {
        return "upcoming_fragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView(ArrayList());
        callUpEventListAPI()
        fabFilter?.setOnClickListener { openBottomSheetDialog() }
        callCategoriesListAPI(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = context?.getUserData<LoginResponse>()
    }

    private fun openBottomSheetDialog() {
        val view = View.inflate(context, R.layout.dialog_filter, null)
        val builder = context?.let { BottomSheetDialogBuilder(it) }
        builder?.customView(view)
        view?.btn_1_week?.setOnClickListener {
            isNearMe = false; callUpEventListAPI(filtersDay = "7");builder?.dismiss();
        }
        view?.btn_2_week?.setOnClickListener {
            isNearMe = false; callUpEventListAPI(filtersDay = "14");builder?.dismiss();
        }
        view?.btn_3_week?.setOnClickListener {
            isNearMe = false;callUpEventListAPI(filtersDay = "21");builder?.dismiss();
        }
        view?.btn_near_me?.setOnClickListener {
            isNearMe = true
            getUserLocation()
            builder?.dismiss();
        }
        view?.btn_category?.setOnClickListener {
            isNearMe = false;
            builder?.dismiss();
            if (categoryList.size > 0) {
                openCategoryBottomSheet()
            } else {
                callCategoriesListAPI(true)
            }
        }
        view?.btn_all?.setOnClickListener {
            isNearMe = false; callUpEventListAPI(); builder?.dismiss();
        }
        view?.btnCancel?.setOnClickListener { builder?.dismiss(); }
        builder?.show()
    }

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
            callUpEventListAPI();
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

    private fun openCategoryBottomSheet() {
        var selectedCategory = ArrayList<CategoryItem?>()
        val view = View.inflate(context, R.layout.dialog_category, null)
        val builder = context?.let { BottomSheetDialogBuilder(it) }
        builder?.customView(view)
        view?.btnCategoryDone?.setOnClickListener {
            callUpEventListAPI(
                selectedCategory = selectedCategory
            ); builder?.dismiss()
        }
        categoryBuilder = view?.rvCategory?.setUp(
            R.layout.row_dialog_category,
            categoryList,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                if (item.isSelected) {
                    view?.imgSelected.visibility = View.VISIBLE
                } else {
                    view?.imgSelected.visibility = View.GONE
                }
                view?.txtCategoryTitle.text = item.name
                view?.setOnClickListener {
                    if (item.isSelected) {
                        item.isSelected = !item.isSelected
                        view?.imgSelected.visibility = View.GONE
                        selectedCategory.remove(item)
                    } else {
                        item.isSelected = !item.isSelected
                        view?.imgSelected.visibility = View.VISIBLE
                        selectedCategory.add(item)
                    }
                }
            }
            isNestedScrollingEnabled = false
        }
        builder?.show()
    }

    private fun callCategoriesListAPI(callAgain: Boolean?) {
//        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getCategories(),
            singleCallback = object : SingleCallback<ArrayList<CategoryItem?>> {
                override fun onSingleSuccess(o: ArrayList<CategoryItem?>, message: String?) {
//                    loadingDialog(false)
                    categoryList = o
                    if (callAgain == true) {
                        if (o.size > 0) {
                            context?.showAppDialog("No Category Available")
                        } else {
                            openCategoryBottomSheet()
                        }
                    }
//                    if (o.size > 0) {
//                        llNoData?.visibility = View.GONE
//                    } else {
//                        llNoData?.visibility = View.VISIBLE
//                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
//                    loadingDialog(false)
//                    simpleAlert(
//                        getString(R.string.app_name).toUpperCase(),
//                        throwable.message
//                    )
                }

                override fun onError(message: String?) {
//                    loadingDialog(false)
//                    message?.let {
//                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
//                    }
                }
            }
        )
    }

    private fun deleteEvent(eventId: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .delEvent(eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    context?.loadingDialog(false)
                    context?.showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) { callUpEventListAPI() }
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

    private fun participateEvent(eventId: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .partEvent(user_id = user?.userId, event_id = eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    context?.loadingDialog(false)
                    context?.showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) { callUpEventListAPI() }
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

    private fun cancelParticipateEvent(eventId: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .cancelPartEvent(user_id = user?.userId, event_id = eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    context?.loadingDialog(false)
                    context?.showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) { callUpEventListAPI() }
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

    private fun setupRecycleView(o: ArrayList<UpEventResponseItem?>) {
        if (o.size > 0) {
            llNoData?.visibility = View.GONE
            upEventBuilder = rvUpComingEvent?.setUp(
                R.layout.row_upcoming_event,
                o,
                RecyclerViewLayoutManager.LINEAR,
                RecyclerViewLinearLayout.VERTICAL
            ) {
                contentBinder { item, view, position ->
                    if (user?.userTypeId.equals("4") or (user?.userTypeId.equals("3"))) {
                        view?.btnDelete?.text = getString(R.string.delete)
                        view?.btnDelete?.setOnClickListener {
                            context?.confirmationDialog(getString(R.string.app_name).toUpperCase(),
                                getString(R.string.del_message), {
                                    deleteEvent(item.id)
                                })
                        }
                    } else {
                        if (item?.isParticipated == true) {
                            view?.btnDelete?.text = getString(R.string.cancel_participate)
                            view?.btnDelete?.setOnClickListener {
                                context?.confirmationDialog(getString(R.string.app_name).toUpperCase(),
                                    getString(R.string.warning_cancel_participate), {
                                        cancelParticipateEvent(item.id)
                                    })
                            }
                        } else {
                            view?.btnDelete?.text = getString(R.string.participate)
                            view?.btnDelete?.setOnClickListener {
                                if (item.ageRestriction != null) {
                                    context?.confirmationDialog(getString(R.string.app_name).toUpperCase(),
                                        "Are you ${item.ageRestriction} years old or above?", {
                                            participateEvent(item.id)
                                        })
                                } else {
                                    participateEvent(item.id)
                                }
                            }
                        }
                    }
                    context?.let {
                        Glide.with(it).load(ApiClient.BASE_IMAGE_URL + item.fileName)
                            .placeholder(R.drawable.event_smaple)
                            .into(view.row_event_image)
                    }
                    if (item.isFavorite == true) {
                        view?.img_whishlist?.setImageResource(R.drawable.ic_heart_fill)
                    } else {
                        view?.img_whishlist?.setImageResource(R.drawable.ic_heart)
                    }
                    view?.img_whishlist?.setOnClickListener {
                        if (item.isFavorite == true) {
                            callUnFavAPI(item, position)
                        } else {
                            callFavAPI(item, position)
                        }
                    }
                    view?.setOnClickListener {
                        var eventIntent = Intent(context, EventDetailActivity::class.java)
                        eventIntent.putExtra("eventId", item.id)
                        startActivityForResult(eventIntent, 1005)
                    }
                    view?.txtOrganisationName.text = item.name
                    view?.txtLocation.text = item.location
                    view?.txtEventDesc?.text = item.description
                    view?.txtEventDate.text =
                        item.date.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.time
                    view?.txtEventUpDate.text =
                        item.endDate.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.endTime


                    view?.txtAddToCalendar?.setOnClickListener {
                        val beginTime: Calendar = Calendar.getInstance()
                        beginTime.time = (item.date + " " + item.time).toDate("dd/MM/yyyy HH:mm")
                        val endTime: Calendar = Calendar.getInstance()
                        endTime.time =
                            (item.endDate + " " + item.endTime).toDate("dd/MM/yyyy HH:mm")
                        val intent: Intent = Intent(Intent.ACTION_INSERT)
                            .setData(Events.CONTENT_URI)
                            .putExtra(
                                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                beginTime.getTimeInMillis()
                            )
                            .putExtra(
                                CalendarContract.EXTRA_EVENT_END_TIME,
                                endTime.getTimeInMillis()
                            )
                            .putExtra(Events.TITLE, item.name)
                            .putExtra(Events.DESCRIPTION, item.description)
                            .putExtra(Events.EVENT_LOCATION, item.location)
                            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
//                        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
                        startActivity(intent)
                    }
                }
                isNestedScrollingEnabled = false
            }
        } else {
            llNoData?.visibility = View.VISIBLE
        }
    }

    private fun callFavAPI(item: UpEventResponseItem, position: Int) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .favouriteEventAPI(
                    event_id = item.id,
                    user_who_favourited_id = context?.getUserId()
                ),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
                    item.isFavorite = true
                    upEventBuilder?.notifyItemChanged(position)
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

    private fun callUnFavAPI(item: UpEventResponseItem, position: Int) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .unFavouriteEventAPI(
                    event_id = item.id,
                    user_who_favourited_id = context?.getUserId()
                ),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
                    item.isFavorite = false
                    upEventBuilder?.notifyItemChanged(position)
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

    private fun callUpEventListAPI(
        filtersDay: String? = null,
        selectedCategory: ArrayList<CategoryItem?> = ArrayList<CategoryItem?>(),
    ) {
        context?.loadingDialog(true)
        val categories = JsonArray();
        selectedCategory?.forEach { categories.add(it?.id) }
        val json = JsonObject()
        json.add("category", categories)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        var observation = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
            .getEvents(body, filter_days = filtersDay)
        if (user?.userTypeId.equals("3")) {
            observation = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getOrgEvents(body, filter_days = filtersDay)
        }
        subscribeToSingle(
            observable = observation,
            singleCallback = object : SingleCallback<ArrayList<UpEventResponseItem?>> {
                override fun onSingleSuccess(o: ArrayList<UpEventResponseItem?>, message: String?) {
                    context?.loadingDialog(false)
                    if (isNearMe == true) {
                        var nearMeList = ArrayList<UpEventResponseItem?>()
                        if (mCurrentLocation != null) {
                            val geocoder = Geocoder(activity, Locale.getDefault())
                            try {
                                val addresses: List<Address?>? = mCurrentLocation?.latitude?.let {
                                    mCurrentLocation?.longitude?.let { it1 ->
                                        geocoder.getFromLocation(
                                            it, it1, 1
                                        )
                                    }
                                }

                                addresses?.forEach outre@{ addressOut ->
                                    o.forEach {
                                        val addressesIn: List<Address?>? =
                                            mCurrentLocation?.latitude?.let {
                                                mCurrentLocation?.longitude?.let { it1 ->
                                                    geocoder.getFromLocation(
                                                        it, it1, 1
                                                    )
                                                }
                                            }
                                        addressesIn?.forEach inner@ { addressInn ->
                                                if (addressInn?.locality?.toLowerCase()?.contains(
                                                        addressOut?.locality?.toLowerCase()
                                                            .toString()
                                                    ) == true
                                                ) {
                                                    nearMeList?.add(it)
                                                    return@inner
                                                }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        setupRecycleView(nearMeList)
                    } else {
                        setupRecycleView(o)
                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    context?.loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
                    context?.simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    context?.loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
                    message?.let {
                        context?.simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1005) {
            if (resultCode == RESULT_OK) {
                callUpEventListAPI()
            }
        }
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getUserLocation()
                }
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
    }

}
