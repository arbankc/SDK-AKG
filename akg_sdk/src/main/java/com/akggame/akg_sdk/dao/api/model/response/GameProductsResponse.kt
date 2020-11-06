package com.akggame.akg_sdk.dao.api.model.response

import androidx.annotation.Keep
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
class GameProductsResponse:BaseResponse()  {

     var meta: MetaBean? = null
     var data: List<ProductData> = listOf(ProductData())

    fun getListOfSKU(datas:List<ProductData>?):List<String>{
        val listOfSKU =ArrayList<String>()
        datas?.forEach {
            listOfSKU.add(it.attributes?.product_code!!)
        }

        return listOfSKU
    }

}
