package com.akggame.akg_sdk.dao.api.model.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
open class BaseResponse {

    @JsonProperty("data")
    @Keep
    var BaseDataResponse: DataBean? = null
    @JsonProperty("meta")
    @Keep
    var BaseMetaResponse: MetaBean? = null

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    class DataBean {
        var message: String? = null
        var token: String? = null
    }

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    class MetaBean {
        var status: Boolean = false
        var code: Int = 0
    }
}
