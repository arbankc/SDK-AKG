package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
class PhoneAuthRequest {


    /**
     * phone_number : 081222647456
     * password : password
     * auth_provider : akg
     * game_provider : lockdown
     * device_id : 1
     * phone_model : Xiaomi
     * operating_system : Android
     */

    var phone_number: String? = null
    var password: String? = null
    var auth_provider: String? = null
    var game_provider: String? = null
    var device_id: String? = null
    var phone_model: String? = null
    var operating_system: String? = null
}

