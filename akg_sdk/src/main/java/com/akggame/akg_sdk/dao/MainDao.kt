package com.akggame.akg_sdk.dao

import android.content.Context
import com.akggame.akg_sdk.dao.api.Api
import com.akggame.akg_sdk.dao.api.model.request.*
import com.akggame.akg_sdk.dao.api.model.response.*
import com.clappingape.dplkagent.model.api.request.DepositRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainDao() {

    fun onProviderAuth(model: FacebookAuthRequest): Observable<FacebookAuthResponse> {
        return Api.onProviderLogin(model)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onPhoneAuth(model: PhoneAuthRequest): Observable<PhoneAuthResponse> {
        return Api.onPhoneLogin(model)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onGuestAuth(model: GuestLoginRequest, context: Context): Observable<PhoneAuthResponse> {
        return Api.onGuestLogin(model, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onLogout(context: Context): Observable<BaseResponse> {
        return Api.onLogout(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onSendOtp(model: SendOtpRequest): Observable<BaseResponse> {
        return Api.onSendOtp(model)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onCheckOtp(model: SendOtpRequest): Observable<BaseResponse> {
        return Api.onCheckOtp(model)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onSignUp(model: SignUpRequest): Observable<BaseResponse> {
        return Api.onSignUp(model)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onUpdatePassword(model: UpdatePasswordRequest): Observable<BaseResponse> {
        return Api.onUpdatePassword(model)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onCheckCurrentUser(context: Context): Observable<CurrentUserResponse> {
        return Api.onGetCurrentUser(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onChangePassword(body: ChangePasswordRequest, context: Context): Observable<BaseResponse> {
        return Api.onChangePassword(body, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onGetProduct(gameProvider: String?, context: Context): Observable<GameProductsResponse> {
        return Api.onGetProduct(gameProvider, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onBindProduct(body: BindSocMedRequest, context: Context): Observable<BaseResponse> {
        return Api.onBindAccount(body, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onBindPhoneNumber(body: PhoneBindingRequest, context: Context): Observable<BaseResponse> {
        return Api.onBindPhoneNumber(body, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onPostOrder(body: PostOrderRequest, context: Context): Observable<BaseResponse> {
        return Api.onPostOrder(body, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onGetSDKVersion(context: Context): Observable<SDKVersionResponse> {
        return Api.onCallGetSDKVersion(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onGetSDKConfig(gameProvider: String?): Observable<SDKConfigResponse> {
        return Api.onCallGetSDKConfig(gameProvider)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onGetBanner(context: Context): Observable<BannerResponse> {
        return Api.onGetBanner(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun onCreateDeposit(context: Context, body: DepositRequest): Observable<DepositResponse> {
        return Api.onCreateDeposit(body, context).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}