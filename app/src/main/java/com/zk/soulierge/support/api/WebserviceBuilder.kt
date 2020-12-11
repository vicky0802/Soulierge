package com.zk.soulierge.support.api

import com.zk.soulierge.support.api.model.CategoryItem
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.OrganisationModalItem
import io.reactivex.Observable
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


}