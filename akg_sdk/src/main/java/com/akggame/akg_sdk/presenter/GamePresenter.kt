package com.akggame.akg_sdk.presenter

import android.content.Context
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.GameListResponse
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.dialog.menu.GameListIView

class GamePresenter(val mIView: IView) {
    fun onGameList(context: Context) {
        (mIView as GameListIView).doOnLoadingGameList(true)
        MainDao().onGetGameList(context)
            .subscribe(object : RxObserver<GameListResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    t as GameListResponse
                    if (t.meta?.code == 200) {
                        (mIView as GameListIView).doOnSuccessGameList(t)
                        (mIView as GameListIView).doOnLoadingGameList(false)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    (mIView as GameListIView).doOnErrorGameList(e.message.toString())
                    (mIView as GameListIView).doOnLoadingGameList(false)
                }
            })
    }
}