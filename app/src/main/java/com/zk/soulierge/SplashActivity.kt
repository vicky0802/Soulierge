package com.zk.soulierge

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.isLogin
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.ActivityNavigationUtility
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        var str:String = editText?.text.toString().trim()
//        var test = test()
//        test.computeSHAHash(str)
//        callFacebookLoginAPI()
        Handler().postDelayed({
            if (isLogin()) {
                ActivityNavigationUtility.navigateWith(this)
                    .setClearStack().navigateTo(MainActivity::class.java)
            } else {
                ActivityNavigationUtility.navigateWith(this)
                    .navigateTo(LandingActivity::class.java)
            }
            finish()
//            viewModel.shouldShowEnterButton.set(true)
        }, 2000)
    }


    private fun callFacebookLoginAPI() {
//        val jSONObject = JSONObject()
//        try {
//            jSONObject.accumulate("user_id", "42936")
//            jSONObject.accumulate(
//               "domain_key",
//                "1"
//            )
//            jSONObject.accumulate(
//                "token",
//                "2bd0742a76015f6e3952aef9a481aecbb62e6880302e31393733383030302031363131343738313534"
//            )
//        } catch (e: JSONException) {
////            AppLog.m357e((getMvpView() as IMyCourseView).getClass().getSimpleName(), e.toString())
//        }

//        val jSONObject = JSONObject()
//        try {
//            jSONObject.accumulate("category_id", "")
//            jSONObject.accumulate("user_id", "42936")
//            jSONObject.accumulate(
//                "domain_key",
//                "1"
//            )
//            jSONObject.accumulate(
//                "token",
//                "2bd0742a76015f6e3952aef9a481aecbb62e6880302e31393733383030302031363131343738313534"
//            )
//            jSONObject.accumulate("limit", 10)
//            jSONObject.accumulate("offset",1)
//        } catch (e: JSONException) {
////            AppLog.m357e((getMvpView() as IBrowseView).getClass().getSimpleName(), e.toString())
//        }
//        val jSONObject = JSONObject()
//        try {
//            jSONObject.accumulate("course_id", "49")
//                        jSONObject.accumulate("user_id", "42936")
//            jSONObject.accumulate(
//                "domain_key",
//                "1"
//            )
//            jSONObject.accumulate(
//                "token",
//                "2bd0742a76015f6e3952aef9a481aecbb62e6880302e31393733383030302031363131343738313534"
//            )
//        } catch (e: JSONException) {
////            AppLog.m357e((getMvpView() as ICurriculumView).getClass().getSimpleName(), e.toString())
//        }

        val jSONObject = JSONObject()
        try {
            jSONObject.accumulate("app_version", "latest")
            jSONObject.accumulate("lecture_id", "279")
            jSONObject.accumulate("course_id", "3")
                        jSONObject.accumulate("user_id", "42936")
            jSONObject.accumulate(
                "domain_key",
                "1"
            )
            jSONObject.accumulate(
                "token",
                "2bd0742a76015f6e3952aef9a481aecbb62e6880302e31393733383030302031363131343738313534"
            )
        } catch (e: JSONException) {
        }
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                .getLectureDetails(
                    RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        jSONObject.toString()
                    )
                ),
            singleCallback = object : SingleCallback<Any?> {
                override fun onSingleSuccess(o: Any?, message: String?) {
                    loadingDialog(false)
                    JSONObject(Gson().toJson(o)).toString()
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