package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

data class OrganisationModalItem(
    @SerializedName("contactNumber")
    var contactNumber: String? = null,
    @SerializedName("country")
    var country: String? = null,
    @SerializedName("dateCreated")
    var dateCreated: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("fileName")
    var fileName: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("isFavorite")
    var isFavorite: Boolean? = null,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("longitude")
    var longitude: Double? = null,
    @SerializedName("name")
    var name: String? = null
)
