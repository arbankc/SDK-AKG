package com.akggame.akg_sdk.ui.dialog.menu

import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.rx.IView

interface BindAccountIView : IView {

    fun doOnSuccess(data:BaseResponse,socmedType:String)

    fun doOnError(message:String,socmedType: String)
}