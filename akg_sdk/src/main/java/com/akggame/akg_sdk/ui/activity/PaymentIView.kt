package com.akggame.akg_sdk.ui.activity

import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.akggame.akg_sdk.rx.IView

interface PaymentIView : IView {

    fun doOnError(message: String)
    fun doOnSuccessPost(data: GameProductsResponse)
    fun doShowProgress(isShow: Boolean)


}