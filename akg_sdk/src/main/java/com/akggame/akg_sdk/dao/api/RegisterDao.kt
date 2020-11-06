package com.akggame.akg_sdk.dao.api

import com.akggame.akg_sdk.dao.api.model.request.SendOtpRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RegisterDao() {

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
}