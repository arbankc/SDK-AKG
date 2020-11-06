package com.akggame.akg_sdk.ui.component

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.akggame.android.sdk.R


@DslMarker
annotation class IconFormMarker

/** creates an instance of [IconForm] from [IconForm.Builder] using kotlin dsl. */
@IconFormMarker
fun iconForm(context: Context, block: IconForm.Builder.() -> Unit): IconForm =
    IconForm.Builder(context).apply(block).build()

/**
 * IconForm is an attribute class what has some attributes about ImageView
 * for customizing [SubmarineView] navigation icon image easily.
 */
class IconForm(builder: Builder) {

    val iconSize = builder.iconSize
    val iconColor = builder.iconColor
    val iconScaleType = builder.iconScaleType

    /** Builder class for[IconForm]. */
    class Builder(context: Context) {
        @JvmField
        var iconSize = 40
        @JvmField
        var iconColor = ContextCompat.getColor(context, R.color.grey)
        @JvmField
        var iconScaleType = ImageView.ScaleType.FIT_XY

        fun setIconSize(value: Int): Builder = apply { this.iconSize = value }
        fun setIconTint(value: Int): Builder = apply { this.iconColor = value }
        fun setIconScaleType(value: ImageView.ScaleType): Builder = apply { this.iconScaleType = value }
        fun build(): IconForm {
            return IconForm(this)
        }
    }
}