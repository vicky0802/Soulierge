package com.zk.soulierge.support.api.model

import com.google.gson.annotations.SerializedName

class GeneralResponse(
    @SerializedName("success")
    var success: String? = null,
    @SerializedName("failure")
    var failure: String? = null
)