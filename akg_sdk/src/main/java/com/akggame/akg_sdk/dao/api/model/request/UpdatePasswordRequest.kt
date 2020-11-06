package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep

@Keep
class UpdatePasswordRequest {

    var phone_number: String? = null
    var auth_provider: String? = null
    var game_provider: String? = null
    var password: String? = null
}
