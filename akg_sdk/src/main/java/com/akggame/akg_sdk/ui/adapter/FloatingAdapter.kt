package com.akggame.akg_sdk.ui.adapter

import android.view.View
import com.akggame.akg_sdk.dao.api.model.SectionRow
import com.akggame.akg_sdk.ui.component.ItemWrapper
import com.akggame.android.sdk.R

class FloatingAdapter(private val listener:FloatingAdapterListener?=null) : BaseAdapter() {

    init {
        addSection(ArrayList<ItemWrapper>())
    }

    internal fun addItem(floatingItem: ItemWrapper) {
        addItemOnSection(0, floatingItem)
        updateItemSize()
    }

    internal fun addItemList(floatingItemList: List<ItemWrapper>) {
        addItemListOnSection(0, floatingItemList)
        updateItemSize()
    }

    internal fun removeItem(floatingItem: ItemWrapper) {
        removeItemOnSection(0, floatingItem)
        updateItemSize()
    }

    internal fun clearAllItems() {
        clearSection(0)
        notifyDataSetChanged()
    }

    private fun updateItemSize() {
        for (item in sectionItems<ItemWrapper>(0)) {
            item as ItemWrapper
            item.itemSize = sectionItems<ItemWrapper>(0).size
        }
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow) = R.layout.item_floating_button

    override fun viewHolder(layout: Int, view: View) = FloatingViewHolder(listener, view)

}