package com.akggame.akg_sdk.dao.api.model.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
class BannerResponse : BaseResponse() {


    var meta: BaseResponse.MetaBean? = null
    var data: List<DataBean>? = null

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    class DataBean {

        var id: String? = null
        var type: String? = null
        var attributes: AttributesBean? = null

        @Keep
        @JsonIgnoreProperties(ignoreUnknown = true)
        class AttributesBean {

            var title: String? = null
            var image_link_url: String? = null
            var cover_image: CoverImageBean? = null

            @Keep
            @JsonIgnoreProperties(ignoreUnknown = true)
            class CoverImageBean {

                var url: String? = null
                var thumb: ThumbBean? = null

                @Keep
                @JsonIgnoreProperties(ignoreUnknown = true)
                class ThumbBean {

                    var url: String? = null
                }
            }

            @Keep
            @JsonIgnoreProperties(ignoreUnknown = true)
            class GameBean {

                var id: String? = null
                var name: String? = null
                var platform: String? = null
                var product_code: String? = null
            }
        }
    }
}
