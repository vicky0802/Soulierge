package com.zk.soulierge.support.api.model

import android.util.Log.e
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject


data class ApiResponse<T>(
    @SerializedName("data")
    val `data`: T?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
)

inline fun <reified T> String.parseTo(): T? {
    return try {
        val data = JSONObject(this)
//        e("Response=>$data")
        Gson().fromJson(data.toString(), T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <reified T> String.parseArray(): ArrayList<T>? {
    return try {
        val data = JSONArray(this)
//        e("Response Array=>$data")
        val listType = object : TypeToken<T>() {}.type
        Gson().fromJson(data.toString(), listType)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <reified T> T.toJson(): String? {
    return try {
        val listType = object : TypeToken<T>() {}.type
        val mjson = Gson().toJson(this, listType)
//        this?.e("Response Array=>$mjson")
        mjson
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
