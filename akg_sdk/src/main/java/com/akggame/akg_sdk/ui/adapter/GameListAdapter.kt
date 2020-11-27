package com.akggame.akg_sdk.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akggame.akg_sdk.`interface`.OnClickItem
import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.layout_item_menulist.view.*

class GameListAdapter : RecyclerView.Adapter<GameListAdapter.ViewHolder>() {
    var dataItemGameList: MutableList<DataItemGameList>? = null
    var indextPos: Int = 0
    var onClickItem: OnClickItem? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_menulist, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dataItemGame = dataItemGameList?.get(position)
        viewHolder.apply {
            itemView.tvNameGame.text = dataItemGame?.attributes?.name

            viewHolder.itemView.setOnClickListener {
                notifyDataSetChanged()
                indextPos = position
                onClickItem?.clickItem(position)
            }

        }

        if (indextPos == position) {
            viewHolder.itemView.llSelectGame.setBackgroundResource(R.drawable.rounded_select_game)
        } else {
            viewHolder.itemView.llSelectGame.setBackgroundResource(R.drawable.rounded_rectangle_radius_white)
        }
    }

    override fun getItemCount(): Int {
        return dataItemGameList!!.size
    }

}