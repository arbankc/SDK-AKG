package com.akggame.akg_sdk.callback

import com.akggame.akg_sdk.dao.api.model.response.DepositResponse

interface OttoPaySDKCallback {
    fun onSuccessPayment(depositResponse: DepositResponse?)
    fun onFailedPayment(message: String)
}