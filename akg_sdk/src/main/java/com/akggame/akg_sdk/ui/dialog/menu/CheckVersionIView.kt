package com.akggame.akg_sdk.ui.dialog.menu

import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.SDKVersionResponse
import com.akggame.akg_sdk.rx.IView

interface CheckVersionIView : IView {

    fun doOnSuccess(data: SDKVersionResponse)

}