package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep

@Keep
class SignUpRequest {

    var phone_number: String? = null
    var password: String? = null
    var auth_provider: String? = null
    var game_provider: String? = null
    var device_id: String? = null
    var phone_model: String? = null
    var operating_system: String? = null
}
