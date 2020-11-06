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
        var is_first_login:Boolean = false
        var message: String? = ""
        var token:String? = ""
    }

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    class MetaBean {
        var status: Boolean = false
        var code: Int = 0
    }
}