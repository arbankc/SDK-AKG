package com.akggame.akg_sdk.callback

interface StatusOttoPayCallback {
    fun onSuccess(status: String)
    fun onFailed(status: String)
}