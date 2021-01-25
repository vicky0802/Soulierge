package com.zk.soulierge.support.api.model


import com.google.gson.annotations.SerializedName

    data class FavOrgResponseItem(
        @SerializedName("id")
        var id: Int? = null,
        @SerializedName("organization")
        var organization: OrganisationModalItem? = null,
        @SerializedName("organizationId")
        var organizationId: Int? = null,
        @SerializedName("userWhoFavouritedId")
        var userWhoFavouritedId: Int? = null
    )
