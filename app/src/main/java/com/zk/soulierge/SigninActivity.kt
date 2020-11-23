package com.zk.soulierge

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.ApiResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.ActivityNavigationUtility
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        tv_forgot_password.setOnClickListener {
            ActivityNavigationUtility.navigateWith(this)
                .navigateTo(ResetPasswordActivity::class.java)
        }

        btn_sign_in.setOnClickListener {
            if (isValid()){
                callLoginAPI()
            }
//            ActivityNavigationUtility.navigateWith(this)
//                .setClearStack().navigateTo(MainActivity::class.java)
        }

//        callLoginAPI()
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
            singleCallback = object : SingleCallback<ApiResponse<Any>> {
                override fun onSingleSuccess(o: ApiResponse<Any>, message: String?) {
                    loadingDialog(false)
                    ActivityNavigationUtility.navigateWith(this@SigninActivity)
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
}
