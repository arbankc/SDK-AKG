package com.akggame.akg_sdk.ui.dialog.register

import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.rx.IView

interface SetPasswordIView : IView {
    fun doOnSuccess(data:BaseResponse)
    fun doOnError()
}