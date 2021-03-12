package com.akggame.akg_sdk.util

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.dao.api.model.request.PostOrderRequest

class JSBridge(
    val activity: Activity,
    val userId: String,
    val gameProductId: String
) : BaseActivity() {

    @JavascriptInterface
    fun closeWindow(status: String) {
        if (status.equals("success", ignoreCase = true)) {
            val bundle = Bundle()
            bundle.putString("UID", userId)
            bundle.putString("GameProductId", gameProductId)
            bundle.putString("TimesTamp", createTimestamp())
            bundle.putString("status", "success")
            hitEventFirebase(Constants.PURCHASE_SUKSES, bundle)
        } else if (status.equals("failed", ignoreCase = true)) {
            val bundle = Bundle()
            bundle.putString("UID", userId)
            bundle.putString("GameProductId", gameProductId)
            bundle.putString("TimesTamp", createTimestamp())
            bundle.putString("status", "failed")
            hitEventFirebase(Constants.PURCHASE_FAILED, bundle)
        }
    }
}