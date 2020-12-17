package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

data class CategoryItem(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    var isSelected:Boolean = false
)