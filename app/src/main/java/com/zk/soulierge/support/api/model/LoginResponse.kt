package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    var accessToken: String? = null,
    @SerializedName("date_created")
    var dateCreated: String? = null,
    @SerializedName("expiry_date")
    var expiryDate: String? = null,
    @SerializedName("failure")
    var failure: String? = null,
    @SerializedName("file_name")
    var fileName: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("success")
    var success: String? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("user_type_id")
    var userTypeId: String? = null
)