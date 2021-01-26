package com.akggame.akg_sdk.dao.api

import android.app.Application
import android.content.Context
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.request.*
import com.akggame.akg_sdk.dao.api.model.response.*
import com.akggame.akg_sdk.util.CacheUtil
import com.clappingape.dplkagent.model.api.request.DepositRequest
import com.orhanobut.hawk.Hawk
import io.reactivex.Observable
import java.util.HashMap

class Api {
    companion object {

        private fun initHeader(): Map<String, String> {
            val map = HashMap<String, String>()
            map["Platform"] = "website"
            map["Cache-Control"] = "no-store"
            map["Content-Type"] = "application/json"

            return map
        }

        private fun initHeader(context: Context): Map<String, String> {
            val getToken = Hawk.get<String>(IConfig.SESSION_TOKEN)
            val map = HashMap<String, String>()
            map["Authorization"] = getToken
            map["Cache-Control"] = "no-store"
            map["Content-Type"] = "application/json"

            return map
        }

        private fun initHeaderOttopay(context: Context): Map<String, String> {
            val map = HashMap<String, String>()
            map["Authorization"] =
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZmVhYzc5OTktMzliNy00MWMzLTkxOGEtNmRhNjMyZjM1NWNhIiwiZW1haWwiOiIwODEzMDAwMDAwMTAiLCJleHAiOjE2MDY0MzYzODJ9.a-vumVipEhTkTH-SHToyFa7DXWyUHjVNLvSqH1xc8as"
            map["x-api-key"] = IConfig.DPLK_X_API_KEY
            return map
        }

        @Synchronized
        private fun initApiDomain(): IApi {
            return ApiManager.initApiService(
                IConfig.BaseUrl, true, IApi::class.java,
                IConfig.TIMEOUT_LONG_INSECOND, true
            ) as IApi
        }

        @Synchronized
        private fun initApiDomainOttopay(): IApi {
            return ApiManager.initApiService(
                IConfig.BaseUrlDPLK, true, IApi::class.java,
                IConfig.TIMEOUT_LONG_INSECOND, true
            ) as IApi
        }

        @Synchronized
        fun onProviderLogin(model: FacebookAuthRequest): Observable<FacebookAuthResponse> {
            return initApiDomain().callProviderLogin(initHeader(), model)
        }

        @Synchronized
        fun onUpsert(
            context: Context,
            model: FacebookAuthRequest
        ): Observable<FacebookAuthResponse> {
            return initApiDomain().callUpsert(initHeader(context), model)
        }

        @Synchronized
        fun onCallGameList(
            context: Context
        ): Observable<GameListResponse> {
            return initApiDomain().callGameList(initHeader(context), "android")
        }

        @Synchronized
        fun onPhoneLogin(model: PhoneAuthRequest): Observable<PhoneAuthResponse> {
            return initApiDomain().callPhoneLogin(initHeader(), model)
        }

        @Synchronized
        fun onGuestLogin(
            model: GuestLoginRequest,
            context: Context
        ): Observable<PhoneAuthResponse> {
            return initApiDomain().callGuestLogin(initHeader(context), model)
        }

        @Synchronized
        fun onLogout(context: Context): Observable<BaseResponse> {
            return initApiDomain().callLogout(initHeader(context))
        }

        @Synchronized
        fun onSendOtp(model: SendOtpRequest): Observable<BaseResponse> {
            return initApiDomain().callSendOtp(initHeader(), model)
        }

        @Synchronized
        fun onCheckOtp(model: SendOtpRequest): Observable<BaseResponse> {
            return initApiDomain().callCheckOtp(initHeader(), model)
        }

        @Synchronized
        fun onSignUp(model: SignUpRequest): Observable<BaseResponse> {
            return initApiDomain().callSignUp(initHeader(), model)
        }

        @Synchronized
        fun onUpdatePassword(model: UpdatePasswordRequest): Observable<BaseResponse> {
            return initApiDomain().callUpdatePassword(initHeader(), model)
        }

        @Synchronized
        fun onGetCurrentUser(idUser: String, context: Context): Observable<CurrentUserResponse> {
            return initApiDomain().callGetCurrentUser(initHeader(context), idUser)
        }

        @Synchronized
        fun onRequestCurrentUser(
            idUser: String,
            context: Context,
            facebookAuthRequest: FacebookAuthRequest
        ): Observable<FacebookAuthResponse> {
            return initApiDomain().callUpdateUpsert(
                initHeader(context),
                idUser,
                facebookAuthRequest
            )
        }

        @Synchronized
        fun onChangePassword(
            body: ChangePasswordRequest,
            context: Context
        ): Observable<BaseResponse> {
            return initApiDomain().callChangePassword(initHeader(context), body)
        }

        @Synchronized
        fun onGetProduct(
            gameProvider: String?,
            context: Context
        ): Observable<GameProductsResponse> {
            return initApiDomain().callGetProduct(initHeader(context), gameProvider)
        }

        @Synchronized
        fun onBindAccount(body: BindSocMedRequest, context: Context): Observable<BaseResponse> {
            return initApiDomain().callBindAccountSocmed(initHeader(context), body)
        }

        @Synchronized
        fun onBindPhoneNumber(
            body: PhoneBindingRequest,
            context: Context
        ): Observable<BaseResponse> {
            return initApiDomain().callPhoneBinding(initHeader(context), body)
        }

        @Synchronized
        fun onPostOrder(body: PostOrderRequest, context: Context): Observable<BaseResponse> {
            return initApiDomain().callPostOrder(initHeader(context), body)
        }

        @Synchronized
        fun onCallGetSDKVersion(context: Context): Observable<SDKVersionResponse> {
            return initApiDomain().callGetSDKVersion(
                initHeader(context),
                CacheUtil.getPreferenceString(IConfig.SESSION_GAME, context)
            )
        }

        @Synchronized
        fun onCallGetSDKConfig(
            context: Context,
            gameProvider: String?
        ): Observable<SDKConfigResponse> {
            return initApiDomain().callGetSDKConfig(initHeader(context), gameProvider)
        }

        @Synchronized
        fun onGetBanner(context: Context): Observable<BannerResponse> {
            return initApiDomain().callGetBanner(
                initHeader(context),
                CacheUtil.getPreferenceString(IConfig.SESSION_GAME, context)
            )
        }

        @Synchronized
        fun onCreateDeposit(
            body: DepositRequest,
            context: Context
        ): Observable<DepositResponse> {
            return initApiDomainOttopay().createDeposit(initHeader(context), body)
        }


        @Synchronized
        fun onCallEulaDetail(context: Context, gameId: String?): Observable<EulaResponse> {
            return initApiDomain().callEula(initHeader(context), gameId.toString())
        }
    }
}