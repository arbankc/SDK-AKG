package com.akggame.akg_sdk

import com.akggame.akg_sdk.dao.pojo.PurchaseItem

interface PurchaseSDKCallback {

    fun onPurchasedItem(purchaseItem: PurchaseItem)
}