package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep

@Keep
class ChangePasswordRequest {
    var old_password: String? = null
    var password: String? = null
    var password_confirmation: String? = null
}
