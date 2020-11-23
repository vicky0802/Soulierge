package com.zk.soulierge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.google.gson.Gson
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.setIsLogin
import com.zk.soulierge.support.utilExt.setUserData
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.ActivityNavigationUtility
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        btn_sign_up?.setOnClickListener { if (isValid()){
        callSignUpAPI()}
        }
    }
    private fun callSignUpAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java).onSignUp(
                email=edt_email_sign_up?.text.toString().trim(),
                name = edt_first_name_sign_up?.text.toString().trim()+" "+edt_last_name_sign_up?.text.toString().trim(),
                phone_number = edt_phone_sign_up?.text.toString().trim(),
                password =  edt_password_sign_up?.text.toString().trim()

            ),
            singleCallback = object : SingleCallback<LoginResponse> {
                override fun onSingleSuccess(o: LoginResponse, message: String?) {
                    loadingDialog(false)
                    if (o.failure.isNullOrEmpty()) {
                        setIsLogin(true)
                        setUserData(Gson().toJson(o))
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
}
