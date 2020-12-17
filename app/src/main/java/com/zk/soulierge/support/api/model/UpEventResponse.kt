package com.zk.soulierge.support.api.model


import android.os.Parcel
import android.os.Parcelable
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
    var latitude: String? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("longitude")
    var longitude: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("organizationId")
    var organizationId: Int? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("userId")
    var userId: Int? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ageRestriction)
        parcel.writeValue(availableCapacity)
        parcel.writeValue(capacity)
        parcel.writeStringList(category)
        parcel.writeString(date)
        parcel.writeString(dateCreated)
        parcel.writeString(dateModified)
        parcel.writeString(description)
        parcel.writeString(endDate)
        parcel.writeString(endTime)
        parcel.writeValue(eventTypeId)
        parcel.writeString(fileName)
        parcel.writeString(id)
        parcel.writeValue(isFavorite)
        parcel.writeValue(isParticipated)
        parcel.writeString(latitude)
        parcel.writeString(location)
        parcel.writeString(longitude)
        parcel.writeString(name)
        parcel.writeValue(organizationId)
        parcel.writeString(time)
        parcel.writeValue(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpEventResponseItem> {
        override fun createFromParcel(parcel: Parcel): UpEventResponseItem {
            return UpEventResponseItem(parcel)
        }

        override fun newArray(size: Int): Array<UpEventResponseItem?> {
            return arrayOfNulls(size)
        }
    }
}