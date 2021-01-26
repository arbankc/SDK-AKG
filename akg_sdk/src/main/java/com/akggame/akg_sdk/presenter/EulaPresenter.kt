package com.akggame.akg_sdk.presenter

import android.content.Context
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.EulaResponse
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.activity.eula.EulaIView

class EulaPresenter(val ImView: IView) {
    fun onGetEula(context: Context, gameId: String) {
        MainDao()
            .onGetEula(context, gameId)
            .subscribe(object : RxObserver<EulaResponse>(ImView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    t as EulaResponse
                    (ImView as EulaIView).onSuccesEula(t)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    (ImView as EulaIView).handleError(e.message.toString())

                }
            })
    }
}