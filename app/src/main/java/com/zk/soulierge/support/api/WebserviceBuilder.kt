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

    @POST("UserService/getAppResources")
    fun getHomePage(
    ): Observable<ResponseBody>

    @POST("OrganizationService/getOrganizations")
    fun getOrganisation(
    ): Observable<ArrayList<OrganisationModalItem?>>

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
        @Query("address") address: String? = null,
        @Query("file_name") file_name: String? = null,
        @Query("gender") gender: String? = null,
        @Query("latitude") latitude: String? = null,
        @Query("longitude") longitude: String? = null,
        @Query("name") name: String? = null,
        @Query("phone_number") phone_number: String? = null,
        @Query("user_id") user_id: String? = null,
    ): Observable<GeneralResponse>

    @POST("EventService/getEvents")
    fun getEvents(@Body body: RequestBody): Observable<ArrayList<UpEventResponseItem?>>

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
}