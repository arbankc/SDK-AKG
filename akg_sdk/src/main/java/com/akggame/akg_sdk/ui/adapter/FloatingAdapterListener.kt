package com.akggame.akg_sdk.ui.adapter

import com.akggame.akg_sdk.dao.api.model.FloatingItem


interface FloatingAdapterListener {
    fun onItemClick(position: Int, floatingItem: FloatingItem)

}