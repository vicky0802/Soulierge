package com.zk.soulierge

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.*
import com.zk.soulierge.support.utils.*
import com.zk.soulierge.utlities.ActivityNavigationUtility
import kotlinx.android.synthetic.main.activity_signup.*


const val LOCATION_PERMISSION_REQUEST = 1920
const val FACEBOOK_LOGIN = 2019

class SignupActivity : AppCompatActivity(), LocationListener {
    private var callbackManager: CallbackManager?=null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var mCurrentLocation: Location? = null
    var locationManager: LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        getUserLocation()
        callbackManager = CallbackManager.Factory.create();
        btn_sign_up?.setOnClickListener {
            if (isValid()) {
                callSignUpAPI()
            }
        }
        login_button?.setOnClickListener{
            fb_login_button?.performClick()
        }

        // Callback registration
        // Callback registration
        fb_login_button?.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }


    private fun callSignUpAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onSignUp(
                email = edt_email_sign_up?.text.toString().trim(),
                name = edt_first_name_sign_up?.text.toString()
                    .trim() + " " + edt_last_name_sign_up?.text.toString().trim(),
                phone_number = edt_phone_sign_up?.text.toString().trim(),
                password = edt_password_sign_up?.text.toString().trim(),
                latitude = mCurrentLocation?.latitude,
                longitude = mCurrentLocation?.longitude

            ),
            singleCallback = object : SingleCallback<LoginResponse> {
                override fun onSingleSuccess(o: LoginResponse, message: String?) {
                    loadingDialog(false)
                    if (o.failure.isNullOrEmpty()) {
                        setIsLogin(true)
                        setUserData(Gson().toJson(o))
                        o.userId?.let { setUserId(it) }
                        o.name?.let { setUserName(it) }
                        ActivityNavigationUtility.navigateWith(this@SignupActivity)
                            .setClearStack().navigateTo(MainActivity::class.java)
                    } else {
                        showAppDialog(o.failure)
                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    message?.let {
                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    private fun isValid(): Boolean {
        return when {
            edt_first_name_sign_up?.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_first_name)
                )
                false
            }
            edt_last_name_sign_up?.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_last_name)
                )
                false
            }
            edt_phone_sign_up?.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_phone)
                )
                false
            }
            edt_email_sign_up?.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_email)
                )
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(edt_email_sign_up?.text.toString())
                .matches() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_valid_email)
                )
                false
            }
            edt_password_sign_up?.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_password)
                )
                false
            }
            else -> true
        }
    }

    fun getUserLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionGranted(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST
                )
            )
                if (LocationUtils(this).checkLocationService())
                    getLastKnownLocation()
                else
                    LocationUtils(this).displayLocationSettingsRequest()
        } else
            if (LocationUtils(this).checkLocationService())
                getLastKnownLocation()
            else
                LocationUtils(this).displayLocationSettingsRequest()
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        if (mFusedLocationProviderClient == null)
            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
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
                LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationProviderClient?.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }
    val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            mCurrentLocation = locationResult?.lastLocation
            "Latest Latitude : ${mCurrentLocation?.latitude}".logMsg("Location")
            "Latest Longitude : ${mCurrentLocation?.longitude}".logMsg("Location")
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestLiveLocation() {
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            40f,
            this
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data);
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getUserLocation()
                }
            }
        }
    }
    override fun onLocationChanged(p0: Location) {
        if (p0 != null) {
            mCurrentLocation = p0
        }
    }


    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String) {
        TODO("Not yet implemented")
    }
}
