package com.akggame.akg_sdk.dao.pojo

import android.os.Parcel
import android.os.Parcelable

class PurchaseItem() : Parcelable {
    var product_name: String? = ""

    var product_id: String? = ""

    var amount: String? = ""

    constructor(source: Parcel) : this(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PurchaseItem> = object : Parcelable.Creator<PurchaseItem> {
            override fun createFromParcel(source: Parcel): PurchaseItem = PurchaseItem(source)
            override fun newArray(size: Int): Array<PurchaseItem?> = arrayOfNulls(size)
        }
    }
}
