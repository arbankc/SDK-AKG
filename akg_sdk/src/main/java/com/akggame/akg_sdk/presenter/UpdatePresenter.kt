package com.akggame.akg_sdk.presenter

import android.content.Context
import android.util.Log
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.request.ChangePasswordRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.dialog.menu.ChangeIView
import io.reactivex.Observer

class UpdatePresenter(val mIView:IView) {
    fun onChangePassword(model:ChangePasswordRequest,context:Context){
        MainDao().onChangePassword(model,context).subscribe(object : RxObserver<BaseResponse>(mIView,""){
            override fun onNext(t: BaseResponse) {
                super.onNext(t)
                if (t.BaseMetaResponse?.code==200){
                    (mIView as ChangeIView).doOnSuccess(t)
                }else{
                    (mIView as ChangeIView).doOnError(t.BaseDataResponse?.message)
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                Log.d("TESTING API", "onError : "+ e.toString())

            }
        })
    }

}