package com.zk.soulierge.support.api.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class OrgUserModel(
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
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("longitude")
    var longitude: Double? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("nextMilestonePoints")
    var nextMilestonePoints: Int? = null,
    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("userTypeId")
    var userTypeId: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeValue(badge)
        parcel.writeValue(currentPoints)
        parcel.writeString(dateCreated)
        parcel.writeString(dateModified)
        parcel.writeString(email)
        parcel.writeString(gender)
        parcel.writeValue(id)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(name)
        parcel.writeValue(nextMilestonePoints)
        parcel.writeString(phoneNumber)
        parcel.writeValue(userTypeId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrgUserModel> {
        override fun createFromParcel(parcel: Parcel): OrgUserModel {
            return OrgUserModel(parcel)
        }

        override fun newArray(size: Int): Array<OrgUserModel?> {
            return arrayOfNulls(size)
        }
    }

}