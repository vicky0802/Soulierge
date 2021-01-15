package com.zk.soulierge.support.api

import com.google.gson.GsonBuilder
import com.zk.soulierge.BuildConfig
import com.zk.soulierge.support.api.model.ApiResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.base.CoreApp
import com.zk.soulierge.support.utilExt.getUserData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    private val OKHTTP_TIMEOUT = 60 * 10 // second
    private var retrofit: Retrofit? = null
    private var retrofitHeader: Retrofit? = null
    private lateinit var okHttpClient: OkHttpClient
    var BUILD_TYPE_DEBUG = false
        get() = BuildConfig.DEBUG

    // Live Server (Production)
// public var WebServiceUrl = "http://soulierge-env.eba-g8p39p3h.us-east-1.elasticbeanstalk.com/rest"

    // Development Server (Development)
//    public var WebServiceUrl = "http://soulierge-test-env.us-east-1.elasticbeanstalk.com/rest"

    //Live Server (Production)
//    const val BASE_URL = "http://soulierge-env.eba-g8p39p3h.us-east-1.elasticbeanstalk.com/rest/"

    //Development Server (Development)
    const val BASE_URL = "http://soulierge-test-env.us-east-1.elasticbeanstalk.com/rest/"
    const val BASE_IMAGE_URL = "https://soulierge-images.s3.amazonaws.com/"


    //    Live url
    const val BASE_URL_API = BASE_URL  /// Latest url


    /**
     * @return [Retrofit] object its single-tone
     */
    fun clear() {
        retrofit = null
        retrofitHeader = null
    }

    fun getApiClient(): Retrofit {
//        if (retrofit == null) {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .client(getOKHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
//        }
        return retrofit!!
    }

    /**
     * You can create multiple methods for different BaseURL
     *
     * @return [Retrofit] object
     */

    fun getApiClient(baseUrl: String): Retrofit {
        /*val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()*/
        val builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)


        builder.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.header("Content-Type", "application/x-www-form-urlencoded")
            chain.proceed(requestBuilder.build())
        }
        if (BUILD_TYPE_DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(builder.build())
            //.addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    /**
     * settings like caching, Request Timeout, Logging can be configured here.
     *
     * @return [OkHttpClient]
     */
    private fun getOKHttpClient(): OkHttpClient {
        return if (!ApiClient::okHttpClient.isInitialized) {
            val builder = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)

            if (BUILD_TYPE_DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(loggingInterceptor)
//                builder.addNetworkInterceptor(StethoInterceptor())
            }

            builder.addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("content-type", "application/json; charset=UTF-8")
                requestBuilder.header("accept", "application/json")
                requestBuilder.header("cache-control", "no-cache")
                try {
//                    CoreApp.INSTANCE.getSelectedLanguage()
//                        .let { requestBuilder.header("Accept-Language", it) }
//                    val userToken = CoreApp.INSTANCE.getUserData<LoginResponse>()?.token
//                    userToken?.let {
//                        requestBuilder.header(
//                            "Authorization",
//                            "Bearer $it"
//                        )
//                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                var response = chain.proceed(requestBuilder.build())
                if (response.isSuccessful) {
                    var str = response.body()?.string()
                    try {
                        val obj = JSONObject(str)
                        if (obj.has("status"))
                            if (obj.getString("status")?.equals("400") == true)
                                obj.remove("data")
                        str = obj.toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    response = response.newBuilder()
                        .body(ResponseBody.create(response.body()?.contentType(), str)).build()
                }
                response
            }
            okHttpClient = builder.build()
            okHttpClient
        } else {
            okHttpClient
        }
    }


    fun getHeaderClient(header: String? = ""): Retrofit {

        //   Log.e("Header token -", CoreApp.getInstance().getLoginToken())
        if (retrofitHeader == null) {
            val builder = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor())
                .retryOnConnectionFailure(true)
                .connectTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)

            if (BUILD_TYPE_DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(loggingInterceptor)
            }

            val okHttpClient = builder.build()

            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()

            retrofitHeader = Retrofit.Builder()
                .baseUrl(BASE_URL_API)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        }
        return retrofitHeader!!
    }

    private class HeaderInterceptor internal constructor() : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
//            Log.e("Header token 1 -", CoreApp.getInstance().getLoginToken())
            val request = chain.request().newBuilder()
                .addHeader("content-type", "application/json; charset=UTF-8")
                .addHeader("accept", "application/json")
                .addHeader("cache-control", "no-cache")
            try {
                val userToken = CoreApp.INSTANCE.getUserData<LoginResponse>()?.accessToken
                request.addHeader("token", userToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return chain.proceed(request.build())
        }
    }
}

interface SingleCallback<T> {
    /**
     * @param o        Whole response Object
     * @param apiNames [A] to differentiate Apis
     */
    fun onSingleSuccess(o: T, message: String?)

    /**
     * @param throwable returns [Throwable] for checking Exception
     * @param apiNames  [A] to differentiate Apis
     */
    fun onFailure(throwable: Throwable, isDisplay: Boolean)

    fun onError(message: String?)

//    fun onLogout(flag: Boolean?)
}

fun <T> subscribeToSingle(observable: Observable<T>, singleCallback: SingleCallback<T>?) {
    Single.fromObservable(observable)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(object : SingleObserver<T> {
            override fun onSuccess(t: T) {
                try {
                    if (t is ArrayList<*> && t.size > 0) {
                        if (t[0] is ApiResponse<*>) {
                            if ((t[0] as ApiResponse<*>).status == 200 || (t[0] as ApiResponse<*>).status == 202 || (t[0] as ApiResponse<*>).status == 203)
                                singleCallback?.onSingleSuccess(t, (t[0] as ApiResponse<*>).message)
                            else if ((t as ApiResponse<*>).status == 401) {
//                        singleCallback?.onLogout(true)
                            } else
                                singleCallback?.onError((t as ApiResponse<*>).message)
                        } else {
                            singleCallback?.onSingleSuccess(t, "ResponseBody")
                        }
                    } else if (t is ApiResponse<*>) {
                        if ((t as ApiResponse<*>).status == 200 || (t as ApiResponse<*>).status == 202 || (t as ApiResponse<*>).status == 203)
                            singleCallback?.onSingleSuccess(t, t.message)
                        else if ((t as ApiResponse<*>).status == 401) {
//                        singleCallback?.onLogout(true)
                        } else
                            singleCallback?.onError((t as ApiResponse<*>).message)
                    } else {
                        singleCallback?.onSingleSuccess(t, "ResponseBody")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                singleCallback?.onFailure(e, false)
            }
        })
}