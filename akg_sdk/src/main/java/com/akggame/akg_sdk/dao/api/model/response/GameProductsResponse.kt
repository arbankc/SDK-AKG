package com.akggame.akg_sdk.dao.api.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
class GameProductsResponse : Parcelable, BaseResponse() {

    @IgnoredOnParcel
    var meta: MetaBean? = null

    @IgnoredOnParcel
    var data: List<ProductData> = listOf(ProductData())

}
