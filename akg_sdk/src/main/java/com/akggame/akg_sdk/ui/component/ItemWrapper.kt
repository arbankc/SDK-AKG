package com.akggame.akg_sdk.ui.component

import com.akggame.akg_sdk.dao.api.model.FloatingItem

internal data class ItemWrapper(
    val floatingItem: FloatingItem,
    var itemSize: Int,
    val parentWidth: Int,
    var orientation: FloatingButton.ORIENTATION
)