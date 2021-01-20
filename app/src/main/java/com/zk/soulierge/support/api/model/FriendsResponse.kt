package com.zk.soulierge.support.api.model

data class FriendsResponse(
    var friends: ArrayList<UserResponse?>,
    var id: Int,
    var pendingFriends: ArrayList<UserResponse?>,
    var rejectedFriends: ArrayList<UserResponse?>,
    var user: UserResponse
)