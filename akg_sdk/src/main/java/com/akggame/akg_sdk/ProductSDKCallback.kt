package com.akggame.akg_sdk

import com.akggame.akg_sdk.dao.BillingDao
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.android.billingclient.api.SkuDetails

interface ProductSDKCallback {

    fun ProductResult(skuDetails:MutableList<SkuDetails>)
}