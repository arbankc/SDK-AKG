package com.akggame.akg_sdk.ui.dialog.menu

import com.akggame.akg_sdk.rx.IView

interface LogoutIView:IView {
    fun doSuccess()
    fun doError()
}