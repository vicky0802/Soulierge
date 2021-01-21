package com.zk.soulierge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.Gson
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.setIsLogin
import com.zk.soulierge.support.utilExt.setUserData
import com.zk.soulierge.support.utilExt.setUserId
import com.zk.soulierge.support.utilExt.setUserName
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.ActivityNavigationUtility
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signin.fb_login_button
import kotlinx.android.synthetic.main.activity_signin.login_button
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONException
import java.util.*

class SigninActivity : AppCompatActivity() {
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        callbackManager = CallbackManager.Factory.create();
        tv_forgot_password.setOnClickListener {
            ActivityNavigationUtility.navigateWith(this)
                .navigateTo(ResetPasswordActivity::class.java)
        }

        btn_sign_in.setOnClickListener {
            if (isValid()) {
                callLoginAPI()
            }
//            ActivityNavigationUtility.navigateWith(this)
//                .setClearStack().navigateTo(MainActivity::class.java)
        }

        login_button?.setOnClickListener {
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
                        callFacebookLoginAPI(email,name)
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
                exception.printStackTrace()
            }
        })


//        callLoginAPI()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun isValid(): Boolean {
        return when {
            edt_email.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_email)
                )
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(edt_email.text.toString())
                .matches() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_valid_email)
                )
                false
            }
            edt_password.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_password)
                )
                false
            }
            else -> true
        }
    }

    private fun callLoginAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onLogin(
                edt_email.text.toString().trim(), edt_password.text.toString().trim()
            ),
            singleCallback = object : SingleCallback<LoginResponse> {
                override fun onSingleSuccess(o: LoginResponse, message: String?) {
                    loadingDialog(false)
                    if (o.failure.isNullOrEmpty()) {
                        setIsLogin(true)
                        setUserData(Gson().toJson(o))
                        o.userId?.let { setUserId(it) }
                        o.name?.let { setUserName(it) }
                        ActivityNavigationUtility.navigateWith(this@SigninActivity)
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
    private fun callFacebookLoginAPI(email: String, name: String) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).facebookLogin(
                email = email,
                name = name,
            ),
            singleCallback = object : SingleCallback<LoginResponse> {
                override fun onSingleSuccess(o: LoginResponse, message: String?) {
                    loadingDialog(false)
                    if (o.failure.isNullOrEmpty()) {
                        setIsLogin(true)
                        setUserData(Gson().toJson(o))
                        o.userId?.let { setUserId(it) }
                        o.name?.let { setUserName(it) }
                        ActivityNavigationUtility.navigateWith(this@SigninActivity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data);
    }
}
