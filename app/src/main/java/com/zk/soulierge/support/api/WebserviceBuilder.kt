package com.zk.soulierge.support.api

import com.zk.soulierge.support.api.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


/**
 * Declare all the APIs in this class with specific interface
 * e.g. Profile for Login/Register Apis
 */
interface WebserviceBuilder {
    @POST("UserService/loginUser")
    fun onLogin(
        @Query("email") username: String? = null,
        @Query("password") password: String? = null
    ): Observable<LoginResponse>

    @POST("UserService/forgotPassword")
    fun onForgotPassword(
        @Query("email") username: String? = null
    ): Observable<ResponseBody>

    @POST("UserService/registerUser")
    fun onSignUp(
        @Query("user_type_id") user_type_id: Int = 1,
        @Query("email") email: String? = null,
        @Query("password") password: String? = null,
        @Query("name") name: String? = null,
        @Query("phone_number") phone_number: String? = null,
        @Query("gender") gender: String? = "Male",
        @Query("address") address: String? = "",
        @Query("latitude") latitude: Double? = 0.0,
        @Query("longitude") longitude: Double? = 0.0
    ): Observable<LoginResponse>

    @POST("UserService/facebookLogin")
    fun facebookLogin(
        @Query("user_type_id") user_type_id: Int = 2,
        @Query("email") email: String? = null,
        @Query("name") name: String? = null,
        @Query("phone_number") phone_number: String? = "",
        @Query("gender") gender: String? = "",
        @Query("address") address: String? = "",
        @Query("latitude") latitude: Double? = 0.0,
        @Query("longitude") longitude: Double? = 0.0
    ): Observable<LoginResponse>

    @POST("UserService/registerOrganizationUser")
    fun onOrgUserCreate(
        @Query("email") email: String? = null,
        @Query("password") password: String? = null,
        @Query("name") name: String? = null,
        @Query("phone_number") phone_number: String? = null,
        @Query("gender") gender: String? = "Male",
        @Query("address") address: String? = "",
        @Query("latitude") latitude: Double? = 0.0,
        @Query("longitude") longitude: Double? = 0.0,
        @Query("organization_id") organization_id: String? = ""
    ): Observable<GeneralResponse>

    @POST("UserService/getAppResources")
    fun getHomePage(
    ): Observable<ResponseBody>

    @POST("OrganizationService/getOrganizations")
    fun getOrganisation(
    ): Observable<ArrayList<OrganisationModalItem?>>

    @POST("OrganizationService/searchOrganizations")
    fun searchOrganisation(
        @Query("search_query") search_query: String? = "",
    ): Observable<ArrayList<OrganisationModalItem?>>

    @POST("UserService/getOrganizationUsers")
    fun getOrganisationUser(
        @Query("organization_id") organizationId: String? = null
    ): Observable<ArrayList<OrgUserModel?>>

    @POST("EventService/getEventParticipants")
    fun getEventParti(
        @Query("event_id") event_id: String? = null
    ): Observable<ArrayList<EventPartiResponseItem?>>

    @POST("UserService/getfriends")
    fun getFriends(
        @Query("user_id") user_id: String? = null
    ): Observable<FriendsResponse?>

    @POST("UserService/getuserbyemail")
    fun getSearchFiend(
        @Query("email") user_id: String? = null
    ): Observable<FriendsResponse?>

    @POST("UserService/getpendingfriendrequests")
    fun getPendingFriends(
        @Query("user_id") user_id: String? = null
    ): Observable<ArrayList<FriendRequestResponse?>>

    @POST("OrganizationService/addFavouriteOrganization")
    fun favouriteOrgAPI(
        @Query("organization_id") organization_id: String? = null,
        @Query("user_who_favourited_id") user_who_favourited_id: String? = null,
    ): Observable<ResponseBody>

    @POST("OrganizationService/deleteFavouriteOrganization")
    fun unFavouriteOrgAPI(
        @Query("organization_id") organization_id: String? = null,
        @Query("user_who_favourited_id") user_who_favourited_id: String? = null,
    ): Observable<ResponseBody>

    @POST("EventService/getCategories")
    fun getCategories(
    ): Observable<ArrayList<CategoryItem?>>

    @POST("EventService/deleteCategory")
    fun delCategory(
        @Query("id") categoryId: String? = null,
    ): Observable<GeneralResponse>

    @POST("EventService/addCategory")
    fun addCategory(
        @Body category: RequestBody? = null,
    ): Observable<GeneralResponse>

    @POST("EventService/updateCategroy")
    fun updateCategroy(
        @Body category: RequestBody? = null,
    ): Observable<GeneralResponse>

    @Multipart
    @POST("UserService/fileupload")
    fun uploadFile(
        @Part profileImage: MultipartBody.Part?
    ): Observable<UploadFileResponse>

    @POST("UserService/updateAppResources")
    fun updateResources(
        @Query("banner") banner: String? = null,
        @Query("org_banner") org_banner: String? = null,
        @Query("event_banner") event_banner: String? = null,
    ): Observable<ResponseBody>

    @POST("UserService/getUser")
    fun getUser(
        @Query("user_id") user_id: String? = null,
    ): Observable<UserResponse>

    @POST("UserService/updateUser")
    fun updateUser(
        @Query("address") address: String? = "",
        @Query("file_name") file_name: String? = null,
        @Query("gender") gender: String? = null,
        @Query("latitude") latitude: String? = null,
        @Query("longitude") longitude: String? = null,
        @Query("name") name: String? = null,
        @Query("phone_number") phone_number: String? = null,
        @Query("user_id") user_id: String? = null,
    ): Observable<GeneralResponse>

    @POST("EventService/getEvents")
    fun getEvents(
        @Body body: RequestBody,
        @Query("filter_days") filter_days: String? = null,
    ): Observable<ArrayList<UpEventResponseItem?>>

    @POST("EventService/searchEvents")
    fun searchEvents(
        @Body body: RequestBody,
        @Query("search_query") search_query: String? = null,
    ): Observable<ArrayList<UpEventResponseItem?>>

    @POST("EventService/getFavouriteEventsForUser")
    fun getFavEvent(
        @Query("filter_days") filter_days: String? = null,
        @Query("user_id") user_id: String? = null
    ): Observable<ArrayList<FavEventResponseItem?>>


    @POST("EventService/getEventsForOrganization")
    fun getEventForOrg(
        @Query("organization_id") organization_id: String? = null,
        @Query("filter_days") filter_days: String? = null,
        @Body body: RequestBody,
    ): Observable<ArrayList<UpEventResponseItem?>>

    @POST("EventService/addFavouriteEvent")
    fun favouriteEventAPI(
        @Query("event_id") event_id: String? = null,
        @Query("user_who_favourited_id") user_who_favourited_id: String? = null,
    ): Observable<ResponseBody>

    @POST("EventService/deleteFavouriteEvent")
    fun unFavouriteEventAPI(
        @Query("event_id") event_id: String? = null,
        @Query("user_who_favourited_id") user_who_favourited_id: String? = null,
    ): Observable<ResponseBody>

    @POST("OrganizationService/deleteOrganization")
    fun deleteOrgAPI(
        @Query("id") id: String? = null,
    ): Observable<ResponseBody>

    @POST("UserService/updatefriendrequest")
    fun updateRequest(
        @Query("id") id: String? = null,
        @Query("status") status: String? = null,
    ): Observable<ResponseBody>

    @POST("UserService/deleteOrganizationUser")
    fun deleteOrgUserAPI(
            @Query("user_id") id: String? = null,
    ): Observable<ResponseBody>

    @POST("EventService/cancelEventParticipant")
    fun deletePartiUserAPI(
            @Query("user_id") user_id: String? = null,
            @Query("event_id") event_id: String? = null,
    ): Observable<ResponseBody>

    @POST("OrganizationService/addOrganization")
    fun addOrganisation(
        @Query("name") name: String? = null,
        @Query("description") description: String? = null,
        @Query("location") location: String? = null,
        @Query("latitude") latitude: String? = null,
        @Query("longitude") longitude: String? = null,
        @Query("country") country: String? = null,
        @Query("file_name") file_name: String? = null,
        @Query("email") email: String? = "",
        @Query("contact_number") contact_number: String? = "",
    ): Observable<AddOrgResponse>

    @POST("OrganizationService/updateOrganization")
    fun updateOrganization(
        @Query("id") id: String? = null,
        @Query("name") name: String? = null,
        @Query("description") description: String? = null,
        @Query("location") location: String? = null,
        @Query("latitude") latitude: String? = null,
        @Query("longitude") longitude: String? = null,
        @Query("country") country: String? = null,
        @Query("file_name") file_name: String? = null,
        @Query("email") email: String? = "",
        @Query("contact_number") contact_number: String? = "",
    ): Observable<AddOrgResponse>

    @POST("EventService/addEvent")
    fun addEvent(
        @Query("organization_id") organization_id: String? = "",
        @Query("user_id") user_id: String? = "",
        @Query("name") name: String? = "",
        @Query("description") description: String? = "",
        @Query("latitude") latitude: String? = "",
        @Query("longitude") longitude: String? = "",
        @Query("date") date: String? = "",
        @Query("time") time: String? = "",
        @Query("event_type_id") event_type_id: String? = "1",
        @Query("file_name") file_name: String? = "",
        @Query("location") location: String? = "",
        @Query("capacity") capacity: String? = "",
        @Query("end_date") end_date: String? = "",
        @Query("end_time") end_time: String? = "",
        @Query("age_restriction") age_restriction: String? = "",
        @Body body: RequestBody,
    ): Observable<AddOrgResponse>

    @POST("EventService/updateEvent")
    fun updateEvent(
        @Query("id") id: String? = null,
        @Query("organization_id") organization_id: String? = "",
        @Query("user_id") user_id: String? = "",
        @Query("name") name: String? = "",
        @Query("description") description: String? = "",
        @Query("latitude") latitude: String? = "",
        @Query("longitude") longitude: String? = "",
        @Query("date") date: String? = "",
        @Query("time") time: String? = "",
        @Query("event_type_id") event_type_id: String? = "1",
        @Query("file_name") file_name: String? = "",
        @Query("location") location: String? = "",
        @Query("capacity") capacity: String? = "",
        @Query("end_date") end_date: String? = "",
        @Query("end_time") end_time: String? = "",
        @Query("age_restriction") age_restriction: String? = "",
        @Body body: RequestBody,
    ): Observable<AddOrgResponse>

    @POST("EventService/deleteEvent")
    fun delEvent(
        @Query("id") categoryId: String? = null,
    ): Observable<GeneralResponse>

    @POST("EventService/bookEventParticipant")
    fun partEvent(
        @Query("user_id") user_id: String? = null,
        @Query("event_id") event_id: String? = null,
    ): Observable<GeneralResponse>

    @POST("EventService/cancelEventParticipant")
    fun cancelPartEvent(
        @Query("user_id") user_id: String? = null,
        @Query("event_id") event_id: String? = null,
    ): Observable<GeneralResponse>

    @POST("EventService/getEventDetail")
    fun eventDetail(
        @Query("id") eventId: String? = null,
    ): Observable<UpEventResponseItem>

    @POST("OrganizationService/getOrganization")
    fun orgDetail(
        @Query("id") orgId: String? = null,
    ): Observable<OrganisationModalItem>
}