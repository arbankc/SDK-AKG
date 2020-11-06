package com.akggame.akg_sdk.ui.dialog.banner

import com.akggame.akg_sdk.dao.api.model.response.BannerResponse
import com.akggame.akg_sdk.rx.IView

interface BannerIView : IView {

    fun doOnSuccess(data:BannerResponse)

}