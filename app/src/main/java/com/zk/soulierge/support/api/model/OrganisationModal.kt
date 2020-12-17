package com.zk.soulierge.support.api.model


import android.os.Parcel
import android.os.Parcelable
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
    var latitude: String? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("longitude")
    var longitude: String? = null,
    @SerializedName("name")
    var name: String? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contactNumber)
        parcel.writeString(country)
        parcel.writeString(dateCreated)
        parcel.writeString(description)
        parcel.writeString(email)
        parcel.writeString(fileName)
        parcel.writeString(id)
        parcel.writeValue(isFavorite)
        parcel.writeString(latitude)
        parcel.writeString(location)
        parcel.writeString(longitude)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrganisationModalItem> {
        override fun createFromParcel(parcel: Parcel): OrganisationModalItem {
            return OrganisationModalItem(parcel)
        }

        override fun newArray(size: Int): Array<OrganisationModalItem?> {
            return arrayOfNulls(size)
        }
    }
}
