package com.akggame.akg_sdk.presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.request.SendOtpRequest
import com.akggame.akg_sdk.dao.api.model.request.SignUpRequest
import com.akggame.akg_sdk.dao.api.model.request.UpdatePasswordRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.dialog.forget.UpdatePasswordDialog
import com.akggame.akg_sdk.ui.dialog.register.OTPIView
import com.akggame.akg_sdk.ui.dialog.register.SetPasswordDialog
import io.reactivex.disposables.Disposable

class RegisterPresenter(val iView: IView) {

    fun sendOtp(model:SendOtpRequest,context:Context){
        MainDao().onSendOtp(model).subscribe(object:RxObserver<BaseResponse>(iView,""){
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                Log.d("TESTING API", "onSubscribe")
            }

            override fun onComplete() {
                super.onComplete()
                Log.d("TESTING API", "onComplete")
            }

            override fun onNext(t: BaseResponse) {
                super.onNext(t)
                Log.d("TESTING API", "onNext")
                if (t.BaseMetaResponse?.code == 200) {
                    (iView as OTPIView).doOnSuccessGenerate(t)
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                Log.d("TESTING API", "onError: "+ e.toString())
            }
        })
    }

    fun onUpdatePassword(model:UpdatePasswordRequest,context: Context){
        MainDao().onUpdatePassword(model).subscribe(object : RxObserver<BaseResponse>(iView,""){
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                Log.d("TESTING API", "onSubscribe")
            }

            override fun onComplete() {
                super.onComplete()
                Log.d("TESTING API", "onComplete")
            }

            override fun onNext(t: BaseResponse) {
                super.onNext(t)
                Log.d("TESTING API", "onNext")
                if (t.BaseMetaResponse?.code == 200) {
                    (iView as UpdatePasswordDialog).doOnSuccess(t)
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                Log.d("TESTING API", "onError : "+ e.toString())

            }
        })
    }

    fun onSignUp(model:SignUpRequest,context: Context){
        MainDao().onSignUp(model).subscribe(object : RxObserver<BaseResponse>(iView,""){
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                Log.d("TESTING API", "onSubscribe")
            }

            override fun onComplete() {
                super.onComplete()
                Log.d("TESTING API", "onComplete")
            }

            override fun onNext(t: BaseResponse) {
                super.onNext(t)
                Log.d("TESTING API", "onNext")
                if (t.BaseMetaResponse?.code == 200) {
                    (iView as SetPasswordDialog).doOnSuccess(t)
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                } else {
                    (iView as SetPasswordDialog).doOnError()
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                (iView as SetPasswordDialog).doOnError()
                Log.d("TESTING API", "onError: "+ e.toString())
            }
        })
    }

    fun checkOtp(model:SendOtpRequest,context:Context){
        MainDao().onCheckOtp(model).subscribe(object:RxObserver<BaseResponse>(iView,""){
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                Log.d("TESTING API", "onSubscribe")
            }

            override fun onComplete() {
                super.onComplete()
                Log.d("TESTING API", "onComplete")
            }

            override fun onNext(t: BaseResponse) {
                super.onNext(t)
                Log.d("TESTING API", "onNext")
                if (t.BaseMetaResponse?.code == 200) {
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                    (iView as OTPIView).doOnSuccessCheck(t)
                } else {
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                Log.d("TESTING API", "onError: "+ e.toString())

            }
        })
    }


}