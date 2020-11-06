package com.akggame.akg_sdk.dao.api.model.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
class SDKConfigResponse : BaseResponse() {

    var data: DataBean? = null
    var meta: BaseResponse.MetaBean? = null

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    class DataBean {

        var adjust: AdjustBean? = null

        @Keep
        @JsonIgnoreProperties(ignoreUnknown = true)
        class AdjustBean {

            var app_token: String? = null
            var events: List<EventsBean>? = null

            @Keep
            @JsonIgnoreProperties(ignoreUnknown = true)
            class EventsBean {
                var name : String = ""
                var token : String = ""
            }
        }
    }
}
