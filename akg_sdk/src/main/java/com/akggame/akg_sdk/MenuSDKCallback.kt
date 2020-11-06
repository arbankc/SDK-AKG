package com.akggame.akg_sdk

interface MenuSDKCallback {

    fun onLogout()
    fun onSuccessBind(token:String,loginType:String)
    fun onCheckSDK(isUpdated : Boolean)
}