package com.akggame.akg_sdk.dao.api.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ProductData {


    var id: String? = null
    var type: String? = null
    var attributes: AttributesBean? = null
    var message : String?=null

    @JsonIgnoreProperties(ignoreUnknown = true)
    class AttributesBean {

        var name: String? = null
        var product_code: String? = null
        var description: String? = null
        var platform: String? = null
        var price: String? = null
        var status: String? = null
        var game: String? = null
        var image:ImageBean? = null
        var game_id : String? = null

        @JsonIgnoreProperties(ignoreUnknown = true)
        class ImageBean{
            var url:String? = null
            var thumb:ThumbBean? = null

            @JsonIgnoreProperties(ignoreUnknown = true)
            class ThumbBean{
                var url:String? = null
            }
        }
    }
}