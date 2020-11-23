package com.zk.soulierge

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_reset_password.*
import okhttp3.ResponseBody
import org.json.JSONObject


class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        btn_reset_password?.setOnClickListener {
            if (isValid()) {
                callForgotPasswordAPI()
            }
        }
    }

    private fun callForgotPasswordAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                .onForgotPassword(
                    edt_email_forgot.text.toString().trim()
                ),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    try {
                        val responseJson = JSONObject(o.string())
                        if (responseJson.has("failure")) {
                            showAppDialog(responseJson.getString("failure"))
                        } else if (responseJson.has("success")) {
                            showAppDialog(responseJson.getString("success"), { onBackPressed() })
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                    showAppDialog()
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
            edt_email_forgot.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_email)
                )
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(edt_email_forgot.text.toString())
                .matches() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_valid_email)
                )
                false
            }
            else -> true
        }
    }
}
