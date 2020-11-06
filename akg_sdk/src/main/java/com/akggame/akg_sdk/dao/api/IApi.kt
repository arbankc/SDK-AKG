package com.akggame.akg_sdk.dao.api

import com.akggame.akg_sdk.dao.api.model.request.*
import com.akggame.akg_sdk.dao.api.model.response.*
import com.clappingape.dplkagent.model.api.request.DepositRequest
import retrofit2.http.*
import  io.reactivex.Observable

interface IApi {

    @POST("auth/provider/login")
    fun callProviderLogin(@HeaderMap map: Map<String, String>, @Body body: FacebookAuthRequest): Observable<FacebookAuthResponse>

    @POST("auth/login")
    fun callPhoneLogin(@HeaderMap map: Map<String, String>, @Body body: PhoneAuthRequest): Observable<PhoneAuthResponse>

    @POST("auth/guest/login")
    fun callGuestLogin(@HeaderMap map: Map<String, String>, @Body body: GuestLoginRequest): Observable<PhoneAuthResponse>

    @POST("auth/signup/send_otp")
    fun callSendOtp(@HeaderMap map: Map<String, String>, @Body body: SendOtpRequest): Observable<BaseResponse>

    @POST("auth/signup/check_otp")
    fun callCheckOtp(@HeaderMap map: Map<String, String>, @Body body: SendOtpRequest): Observable<BaseResponse>

    @POST("auth/signup")
    fun callSignUp(@HeaderMap map: Map<String, String>, @Body body: SignUpRequest): Observable<BaseResponse>

    @POST("auth/update_password")
    fun callUpdatePassword(@HeaderMap map: Map<String, String>, @Body body: UpdatePasswordRequest): Observable<BaseResponse>

    @GET("sdk_version/android/{game-provider}")
    fun callGetSDKVersion(@HeaderMap map: Map<String, String>,@Path("game-provider") gameProvider: String?): Observable<SDKVersionResponse>

    @GET("sdk_configuration/android/{game-provider}")
    fun callGetSDKConfig(@HeaderMap map: Map<String, String>,@Path("game-provider") gameProvider: String?): Observable<SDKConfigResponse>

    @DELETE("auth/logout")
    fun callLogout(@HeaderMap map: Map<String, String>): Observable<BaseResponse>

    @GET("auth/current_user")
    fun callGetCurrentUser(@HeaderMap map: Map<String, String>): Observable<CurrentUserResponse>

    @PUT("auth/change_password")
    fun callChangePassword(@HeaderMap map: Map<String, String>, @Body body: ChangePasswordRequest): Observable<BaseResponse>

    @GET("game_products/android/{game-provider}")
    fun callGetProduct(@HeaderMap map: Map<String, String>, @Path("game-provider") gameProvider: String?): Observable<GameProductsResponse>

    @POST("account/binding")
    fun callBindAccountSocmed(@HeaderMap map: Map<String, String>, @Body body: BindSocMedRequest):
            Observable<BaseResponse>

    @POST("/account/phone/binding")
    fun callPhoneBinding(@HeaderMap map: Map<String, String>, @Body body: PhoneBindingRequest) :
            Observable<BaseResponse>

    @POST("order/transactions")
    fun callPostOrder(@HeaderMap map: Map<String, String>, @Body body: PostOrderRequest):
            Observable<BaseResponse>

    @GET("/banners/android/{game-provider}")
    fun callGetBanner(@HeaderMap map: Map<String,String>,@Path("game-provider") gameProvider: String?):
            Observable<BannerResponse>

    @POST("/deposit")
    fun createDeposit(
        @HeaderMap headers: Map<String, String>,
        @Body data: DepositRequest
    ): Observable<DepositResponse>
}