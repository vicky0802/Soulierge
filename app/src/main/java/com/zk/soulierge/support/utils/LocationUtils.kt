package com.zk.soulierge.support.utils

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.zk.soulierge.support.utilExt.showToast


const val REQUEST_CHECK_SETTINGS = 1713

class LocationUtils(val mActivity: Activity) {

    companion object {
        var builder: LocationSettingsRequest.Builder? = null

        fun createLocationRequest() {
            val mLocationRequest = LocationRequest()
            mLocationRequest.interval = 10000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest)
        }

        fun cheackLocationSettings(mActivity: Activity) {
            val client = LocationServices.getSettingsClient(mActivity)
            val task = client.checkLocationSettings(builder?.build())

            task.addOnSuccessListener {

            }

            task.addOnFailureListener {
                if (it is ResolvableApiException) {
                    "Please turn on location service from settings".showToast()
                }
            }
        }
    }

    fun checkLocationService(): Boolean {
        val lm = mActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return gps_enabled || network_enabled
    }

    fun displayLocationSettingsRequest() {
        val googleApiClient = GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
        builder?.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(mActivity).checkLocationSettings(builder?.build())

        result.addOnSuccessListener {
            "Location service is working".showToast()
        }

        result.addOnFailureListener {
            if (it is ResolvableApiException) {
                it.startResolutionForResult(mActivity,
                        REQUEST_CHECK_SETTINGS)
            }
        }
    }
}