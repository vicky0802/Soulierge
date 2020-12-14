package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

data class UpEventResponseItem(
    @SerializedName("ageRestriction")
    var ageRestriction: Int? = null,
    @SerializedName("availableCapacity")
    var availableCapacity: Int? = null,
    @SerializedName("capacity")
    var capacity: Int? = null,
    @SerializedName("category")
    var category: List<String?>? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("dateCreated")
    var dateCreated: String? = null,
    @SerializedName("dateModified")
    var dateModified: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("endDate")
    var endDate: String? = null,
    @SerializedName("endTime")
    var endTime: String? = null,
    @SerializedName("eventTypeId")
    var eventTypeId: Int? = null,
    @SerializedName("fileName")
    var fileName: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("isFavorite")
    var isFavorite: Boolean? = null,
    @SerializedName("isParticipated")
    var isParticipated: Boolean? = null,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("longitude")
    var longitude: Double? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("organizationId")
    var organizationId: Int? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("userId")
    var userId: Int? = null
)