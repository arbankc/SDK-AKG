package com.akggame.akg_sdk

import android.content.Context

interface MenuSDKCallback {

    fun onLogout()
    fun onBindAccount(context: Context)
    fun onCheckSDK(isUpdated: Boolean)
    fun onClickEula(context: Context, idGame: String)
    fun onClickFbPage(context: Context)
    fun onContactUs(context: Context)

}