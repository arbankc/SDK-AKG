package com.akggame.akg_sdk.util

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.callback.StatusOttoPayCallback
import com.google.android.gms.measurement.module.Analytics
import com.google.firebase.analytics.FirebaseAnalytics

class JSBridge(
    val activity: Activity,
    val statusOttoPayCallback: StatusOttoPayCallback
) : BaseActivity() {
    @JavascriptInterface
    fun closeWindow(status: String) {
        if (status.equals("success", ignoreCase = true)) {
            statusOttoPayCallback.onSuccess(status)
        } else if (status.equals("failed", ignoreCase = true)) {
            statusOttoPayCallback.onFailed(status)
        }
    }
}