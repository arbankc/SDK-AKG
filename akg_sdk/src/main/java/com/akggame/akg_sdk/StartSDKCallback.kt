package com.akggame.akg_sdk

import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest

interface StartSDKCallback {
    fun onStartGame()
    fun onSuccesStart(facebookAuthRequest: FacebookAuthRequest)
    fun onFailedStart(message: String)
}