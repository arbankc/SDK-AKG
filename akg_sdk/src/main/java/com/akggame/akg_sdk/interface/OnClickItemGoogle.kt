package com.akggame.akg_sdk.`interface`

import com.android.billingclient.api.SkuDetails

interface OnClickItemGoogle {
    fun clickItem(position: Int, skuDetails: SkuDetails)
}