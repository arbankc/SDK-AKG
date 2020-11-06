package com.akggame.akg_sdk.ui.activity

import com.akggame.akg_sdk.dao.api.model.response.DepositResponse
import com.akggame.akg_sdk.rx.IView

interface OttopayIView : IView {
    fun doOnSuccessCreateDeposit(data: DepositResponse)
}