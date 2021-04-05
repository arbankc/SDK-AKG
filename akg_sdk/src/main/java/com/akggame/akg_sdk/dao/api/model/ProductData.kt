package com.akggame.akg_sdk.dao.api.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
class ProductData() : Parcelable {


    var id: String? = null
    var type: String? = null
    var attributes: AttributesBean? = null
    var message: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        type = parcel.readString()
        attributes = parcel.readParcelable(AttributesBean::class.java.classLoader)
        message = parcel.readString()
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    class AttributesBean() : Parcelable {

        var name: String? = null
        var product_code: String? = null
        var description: String? = null
        var platform: String? = null
        var price: String? = null
        var status: String? = null
        var game: String? = null
        var image: ImageBean? = null
        var game_id: String? = null

        constructor(parcel: Parcel) : this() {
            name = parcel.readString()
            product_code = parcel.readString()
            description = parcel.readString()
            platform = parcel.readString()
            price = parcel.readString()
            status = parcel.readString()
            game = parcel.readString()
            game_id = parcel.readString()
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        class ImageBean {
            var url: String? = null
            var thumb: ThumbBean? = null

            @JsonIgnoreProperties(ignoreUnknown = true)
            class ThumbBean {
                var url: String? = null
            }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(product_code)
            parcel.writeString(description)
            parcel.writeString(platform)
            parcel.writeString(price)
            parcel.writeString(status)
            parcel.writeString(game)
            parcel.writeString(game_id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AttributesBean> {
            override fun createFromParcel(parcel: Parcel): AttributesBean {
                return AttributesBean(parcel)
            }

            override fun newArray(size: Int): Array<AttributesBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeParcelable(attributes, flags)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductData> {
        override fun createFromParcel(parcel: Parcel): ProductData {
            return ProductData(parcel)
        }

        override fun newArray(size: Int): Array<ProductData?> {
            return arrayOfNulls(size)
        }
    }
}