package com.akggame.akg_sdk

import android.content.Context

interface MenuSDKCallback {

    fun onLogout()
    fun onSuccessBind(token: String, loginType: String)
    fun onCheckSDK(isUpdated: Boolean)
    fun onClickEula(context: Context)
    fun onClickFbPage(context: Context)
    fun onContactUs(context: Context)

}