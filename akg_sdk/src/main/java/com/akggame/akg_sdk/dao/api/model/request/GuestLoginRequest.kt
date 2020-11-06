package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep

@Keep
data class GuestLoginRequest(
    val auth_provider: String,
    val device_id: String,
    val game_provider: String?,
    val operating_system: String,
    val phone_model: String
)