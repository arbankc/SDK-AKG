package com.akggame.akg_sdk

import com.akggame.akg_sdk.dao.api.model.response.EulaResponse

interface EulaSdkCallBack {
    fun onSuccesEula(eulaReponse: EulaResponse)
    fun onErrorEula(message: String)
}