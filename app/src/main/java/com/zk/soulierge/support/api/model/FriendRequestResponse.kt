package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

data class FriendRequestResponse(
    @SerializedName("friendUser")
    var friendUser: UserResponse? = null,
    @SerializedName("id")
    var id: Int? = null
)
