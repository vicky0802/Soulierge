package com.zk.soulierge

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.UploadFileResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.*
import com.zk.soulierge.support.utils.*
import com.zk.soulierge.utlities.ActivityNavigationUtility
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.fb_login_button
import kotlinx.android.synthetic.main.activity_signup.login_button
import kotlinx.android.synthetic.main.fragment_account_setting.*
import okhttp3.MultipartBody
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


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
            LoginManager.getInstance().logOut()
            fb_login_button?.performClick()
        }

        // Callback registration
        fb_login_button?.setReadPermissions(Arrays.asList("public_profile", "email"));
        fb_login_button?.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                val graphRequest = GraphRequest.newMeRequest(
                    loginResult?.accessToken
                ) { `object`, response ->
                    Log.d("JSON", "" + response.jsonObject.toString())
                    try {
                        var email = `object`.getString("email")
                        var name = `object`.getString("name")
                        val fid = `object`.getString("id")
                        callFacebookLoginAPI(email, name,fid)
//                        https://graph.facebook.com/143990709444026/picture?type=large
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                val parameters = Bundle()
                parameters.putString("fields", "id,name,first_name,last_name,email,gender")
                graphRequest.parameters = parameters
                graphRequest.executeAsync()
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callFacebookLoginAPI(email: String, name: String, fid: String) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                .facebookLogin(
                    email = email,
                    name = name,
                ),
            singleCallback = object : SingleCallback<LoginResponse> {
                override fun onSingleSuccess(o: LoginResponse, message: String?) {
                    loadingDialog(false)
                    if (o.failure.isNullOrEmpty()) {
                        Glide.with(this@SignupActivity).asBitmap().load("https://graph.facebook.com/${fid}/picture?type=large")
                            .listener(
                            object :RequestListener<Bitmap>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    setIsLogin(true)
                                    setUserData(Gson().toJson(o))
                                    o.userId?.let { setUserId(it) }
                                    o.name?.let { setUserName(it) }
                                    ActivityNavigationUtility.navigateWith(this@SignupActivity)
                                        .setClearStack().navigateTo(MainActivity::class.java)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    uploadFileAPI(saveImage(resource)?.toMultipartBody("file"),o)
                                    return false
                                }

                            }
                        ).submit()
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

    private fun saveImage(image: Bitmap?): File? {
        var imageFile: File? = null
        val imageFileName = "JPEG_" + "FILE_NAME" + ".jpg"
        val storageDir = File(cacheDir.absolutePath)
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
             imageFile = File(storageDir, imageFileName)
//            savedImagePath = imageFile.getAbsolutePath()
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return imageFile
    }

    private fun uploadFileAPI(toMultipartBody: MultipartBody.Part?, loginResponse: LoginResponse) {
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .uploadFile(toMultipartBody),
            singleCallback = object : SingleCallback<UploadFileResponse> {
                override fun onSingleSuccess(o: UploadFileResponse, message: String?) {
                    loadingDialog(false)
                    updateUserAPI(o.file_name,loginResponse)
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

    private fun updateUserAPI(fileName: String?, id: LoginResponse) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateUser(
                    file_name = fileName,
                    user_id = id.userId
                ),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    setIsLogin(true)
                    setUserData(Gson().toJson(id))
                    id.userId?.let { setUserId(it) }
                    id.name?.let { setUserName(it) }
                    ActivityNavigationUtility.navigateWith(this@SignupActivity)
                        .setClearStack().navigateTo(MainActivity::class.java)
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
