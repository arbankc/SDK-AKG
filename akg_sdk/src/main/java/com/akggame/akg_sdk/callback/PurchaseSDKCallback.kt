package com.akggame.akg_sdk.callback

import com.akggame.akg_sdk.dao.pojo.PurchaseItem

interface PurchaseSDKCallback {
    fun onPurchasedItem(purchaseItem: PurchaseItem)
}