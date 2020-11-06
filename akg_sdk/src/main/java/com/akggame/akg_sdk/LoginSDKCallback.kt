package com.akggame.akg_sdk


interface LoginSDKCallback {
    fun onResponseSuccess(token: String, username: String, loginType: String)
    fun onResponseFailed(message: String)
}