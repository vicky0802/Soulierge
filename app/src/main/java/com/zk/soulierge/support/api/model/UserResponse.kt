package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("badge")
    var badge: Int? = null,
    @SerializedName("currentPoints")
    var currentPoints: Int? = null,
    @SerializedName("dateCreated")
    var dateCreated: String? = null,
    @SerializedName("dateModified")
    var dateModified: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("failure")
    var failure: String? = null,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("latitude")
    var latitude: String? = null,
    @SerializedName("longitude")
    var longitude: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("nextMilestonePoints")
    var nextMilestonePoints: Int? = null,
    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("userTypeId")
    var userTypeId: String? = null
)