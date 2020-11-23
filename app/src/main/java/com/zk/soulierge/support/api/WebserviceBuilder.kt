package com.zk.soulierge.support.api

import android.os.Build
import com.zk.soulierge.support.api.model.ApiResponse
import com.zk.soulierge.support.base.CoreApp
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


/**
 * Declare all the APIs in this class with specific interface
 * e.g. Profile for Login/Register Apis
 */
interface WebserviceBuilder {
    @FormUrlEncoded
    @POST("rest/UserService/loginUser")
    fun onLogin(
        @Field("email") username: String? = null,
        @Field("password") password: String? = null
    ): Observable<ApiResponse<Any>>

}