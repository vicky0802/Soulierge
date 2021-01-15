package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

data class EventPartiResponseItem(
    @SerializedName("eventId")
    var eventId: Int? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("user")
    var user: OrgUserModel? = null,
    @SerializedName("userId")
    var userId: Int? = null
)