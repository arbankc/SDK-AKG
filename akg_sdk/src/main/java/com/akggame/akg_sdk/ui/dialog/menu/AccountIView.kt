package com.akggame.akg_sdk.ui.dialog.menu

import androidx.appcompat.app.AppCompatActivity
import com.akggame.akg_sdk.dao.api.model.response.CurrentUserResponse
import com.akggame.akg_sdk.rx.IView

interface AccountIView :IView {

    fun doOnSuccess(activity: AppCompatActivity, data:CurrentUserResponse)
    fun doOnError(message:String?)
}