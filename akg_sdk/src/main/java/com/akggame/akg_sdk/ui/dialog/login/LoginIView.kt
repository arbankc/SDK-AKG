package com.akggame.akg_sdk.ui.dialog.login

import com.akggame.akg_sdk.dao.api.model.response.FacebookAuthResponse
import com.akggame.akg_sdk.rx.IView

interface LoginIView : IView {

    fun doOnSuccess(
        facebookAuthResponse: FacebookAuthResponse.DataBean?,
        isFirstLogin: Boolean,
        token: String,
        userId: String,
        typeLogin: String
    )

    fun doOnError(message: String)

}