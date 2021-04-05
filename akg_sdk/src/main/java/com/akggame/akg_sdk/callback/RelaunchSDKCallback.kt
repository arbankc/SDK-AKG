package com.akggame.akg_sdk.callback

interface RelaunchSDKCallback {

    fun onContinue()

    /*
    * In this function, set your logout function and recall AKG_SDK.onLogin() if necessary
    * */
    fun onReLogin()
}