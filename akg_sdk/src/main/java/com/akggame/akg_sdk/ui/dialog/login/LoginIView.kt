package com.akggame.akg_sdk.ui.dialog.login

import com.akggame.akg_sdk.rx.IView

interface LoginIView : IView {

    fun doOnSuccess(isFirstLogin:Boolean,token:String,loginType:String)
    fun doOnError(message:String)
}