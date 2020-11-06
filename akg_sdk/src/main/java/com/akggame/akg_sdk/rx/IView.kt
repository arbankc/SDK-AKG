package com.akggame.akg_sdk.rx

import android.view.View
//import com.akggames.akg_sdk.ui.BaseActivity

interface IView {

     fun handleError(message: String)

//     fun getCurrentActivity(): BaseActivity
//
//     fun getContentView(): View

     fun handleRetryConnection()
}