package com.akggame.akg_sdk.ui.dialog.forget

import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.rx.IView

interface UpdatePasswordIView : IView {
    fun doOnSuccess(data: BaseResponse)
}