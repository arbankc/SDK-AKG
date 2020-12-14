package com.akggame.akg_sdk.ui.activity.eula

import com.akggame.akg_sdk.dao.api.model.response.EulaResponse
import com.akggame.akg_sdk.rx.IView

interface EulaIView : IView {
    fun onSuccesEula(eulaReponse: EulaResponse)
    fun onErrorEula(message: String)

}