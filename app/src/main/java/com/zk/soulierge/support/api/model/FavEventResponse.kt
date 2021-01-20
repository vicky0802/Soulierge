package com.zk.soulierge.support.api.model

data class FavEventResponseItem(
    var event: Event,
    var eventId: Int,
    var id: Int,
    var userWhoFavouritedId: Int
)

data class Event(
    var ageRestriction: Int?,
    var availableCapacity: Int,
    var capacity: Int,
    var category: List<String>,
    var date: String,
    var dateCreated: String,
    var dateModified: String,
    var description: String,
    var endDate: String,
    var endTime: String,
    var eventTypeId: Int,
    var fileName: String,
    var id: Int,
    var isFavorite: Boolean,
    var isParticipated: Boolean,
    var latitude: Double,
    var location: String,
    var longitude: Double,
    var name: String,
    var organizationId: Int,
    var time: String,
    var userId: Int
)

