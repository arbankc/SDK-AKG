package com.akggame.akg_sdk.ui.activity

import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.akggame.akg_sdk.rx.IView
import com.android.billingclient.api.Purchase

interface PaymentIView : IView {

    fun doOnSuccess(data: GameProductsResponse)
    fun doOnError(message: String)
    fun doOnSuccessPost(o:BaseResponse,purchase: Purchase)
    fun doOnComplete(purchase: Purchase)


}