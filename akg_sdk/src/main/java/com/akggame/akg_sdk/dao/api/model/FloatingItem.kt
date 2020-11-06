package com.akggame.akg_sdk.dao.api.model

import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import com.akggame.akg_sdk.ui.component.IconForm
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class FloatingItem(
    val icon: Drawable? = null,
    val iconForm: IconForm? = null,
    val name: String?= ""
)