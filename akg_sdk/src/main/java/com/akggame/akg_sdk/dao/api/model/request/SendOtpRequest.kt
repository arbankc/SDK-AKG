package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep

@Keep
class SendOtpRequest {

    /**
     * phone_number : 081222647453
     * auth_provider : akg
     * game_provider : mobile-legend
     * otp_type : registration
     */

    var phone_number: String? = null
    var auth_provider: String? = null
    var game_provider: String? = null
    var otp_type: String? = null
    var otp_code: String? = null
}
