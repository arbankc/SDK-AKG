package com.akggame.akg_sdk.util

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun ImageView.loadImageFromUrl(url: String, isCircle: Boolean = false) {

    if (isCircle) {
        Glide.with(this.context)
            .load(url)
            .circleCrop()
            .into(this)
    } else {
        Glide.with(this.context)
            .load(url)
            .into(this)
    }

}

fun ImageView.loadImageFromDrawable(int: Int) {
    Glide.with(this.context)
        .load(int)
        .into(this)
}
