package com.akggame.akg_sdk.presenter

import android.content.Context
import android.util.Log
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.request.BindSocMedRequest
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.dao.api.model.request.PhoneBindingRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.FacebookAuthResponse
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.dialog.login.LoginIView
import com.akggame.akg_sdk.ui.dialog.menu.BindAccountIView
import com.akggame.akg_sdk.ui.dialog.register.SetPasswordIView
import io.reactivex.disposables.Disposable

class BindAccountPresenter(val mIView: IView) {


    fun onBindAccount(body: BindSocMedRequest, socmedType: String, context: Context) {
        MainDao().onBindProduct(body, context)
            .subscribe(object : RxObserver<BaseResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    if (t.BaseMetaResponse?.code == 200) {
                        (mIView as BindAccountIView).doOnSuccess(t, socmedType)
                    } else {
                        (mIView as BindAccountIView).doOnError(
                            t.BaseDataResponse?.message.toString(),
                            socmedType
                        )
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : " + e.toString())

                }
            })
    }


    fun updateUpsertUser(
        model: FacebookAuthRequest,
        idUser: String,
        context: Context,
        socmedType: String
    ) {
        MainDao().onRequestUpdatetUser(model, idUser, context)
            .subscribe(object : RxObserver<FacebookAuthResponse>(mIView, "") {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    Log.d("TESTING API", "onSubscribe")
                }

                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    t as FacebookAuthResponse

                    Log.d("TESTING API", "onNext")
                    if (t.meta?.code == 200) {
                        (mIView as BindAccountIView).doOnSuccess(t, socmedType)
                    } else {
                        (mIView as BindAccountIView).doOnError(t.data?.message!!, socmedType)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : " + e.toString())

                }
            })
    }


    fun onBindPhoneNumber(body: PhoneBindingRequest, context: Context) {
        MainDao().onBindPhoneNumber(body, context)
            .subscribe(object : RxObserver<BaseResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    if (t.BaseMetaResponse?.code == 200) {
                        (mIView as SetPasswordIView).doOnSuccess(t)
                    } else {
                        (mIView as SetPasswordIView).handleError(t.BaseDataResponse?.message!!)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : " + e.toString())

                }
            })
    }
}