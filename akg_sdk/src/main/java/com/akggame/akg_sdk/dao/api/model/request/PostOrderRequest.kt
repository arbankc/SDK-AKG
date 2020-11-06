package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep

@Keep
data class PostOrderRequest(
    val channel: String,
    val date: Long,
    val game_name: String,
    val order_id: String,
    val package_name: String,
    val paid_amount: Int,
    val platform: String,
    val product_id: String,
    val purchase_token: String,
    val sku: String,
    val status: String,
    val uid: String?,
    val username: String?
)