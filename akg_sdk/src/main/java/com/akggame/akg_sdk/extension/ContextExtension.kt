package com.akggame.akg_sdk.extension

import android.content.Context
import android.graphics.Point
import android.view.WindowManager


/** gets display size as a point. */
internal fun Context.displaySize(): Point {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

/** dp size to px size. */
internal fun Context.dp2Px(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * 2).toInt()
}

/** dp size to px size. */
internal fun Context.dp2Px(dp: Float): Int {
    val scale = resources.displayMetrics.density
    return (dp * 2).toInt()
}