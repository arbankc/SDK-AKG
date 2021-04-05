package com.akggame.akg_sdk.ui.adapter

import com.akggame.akg_sdk.`interface`.OnClickItem
import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList
import com.akggame.newandroid.sdk.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_item_menulist.view.*

class MenuListGameAdapter(val onClickItem: OnClickItem, val dataItemGameList: DataItemGameList) :
    Item() {
    var indextPos: Int = 0

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tvNameGame.text = dataItemGameList.attributes?.name

            viewHolder.itemView.setOnClickListener {
                notifyChanged()
                indextPos = position
                onClickItem.clickItem(position)
            }

        }

        if (indextPos == position) {
            viewHolder.itemView.llSelectGame.setBackgroundResource(R.drawable.rounded_select_game)
            notifyChanged()
        } else {
            notifyChanged()
            viewHolder.itemView.llSelectGame.setBackgroundResource(R.drawable.rounded_rectangle_radius_white)
        }
    }

    override fun getLayout(): Int = R.layout.layout_item_menulist
}