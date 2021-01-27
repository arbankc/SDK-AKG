package com.akggame.akg_sdk.dao.api.model.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
class FacebookAuthResponse : BaseResponse() {

    var data: DataBean? = null
    var meta: MetaBean? = null

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    class DataBean {
        var is_first_login: Boolean = false
        var message: String? = ""
        var id: String? = ""
        var token: String? = ""
        var attributes: AttributesBean? = null

        @Keep
        @JsonIgnoreProperties(ignoreUnknown = true)
        class AttributesBean {
            var uid: String? = null
            var email: String? = null
            var phone_number: String? = null
            var auth_provider: String? = null
            var created_at: String? = null
            var game_provider: String? = null
            var last_login: String? = null
            var register_time: String? = null
            var event_name: String? = null
            var banned_based_on: String? = null
            var banned_message: String? = null
            var has_banned: Boolean? = null
        }
    }

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    class MetaBean {
        var status: Boolean = false
        var code: Int = 0
    }
}