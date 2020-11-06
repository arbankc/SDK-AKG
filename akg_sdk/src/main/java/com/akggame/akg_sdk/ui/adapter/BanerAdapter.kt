package com.akggame.akg_sdk.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.akggame.akg_sdk.dao.api.model.response.BannerResponse
import com.akggame.akg_sdk.ui.activity.WebView
import com.akggame.android.sdk.R
import com.squareup.picasso.Picasso

class BanerAdapter(val context : Context) : PagerAdapter() {

    var listData = mutableListOf<BannerResponse.DataBean>()
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image = ImageView(context)
        var data = listData.get(position)
        Picasso.get().load(data.attributes?.cover_image!!.url).into(image)

        image.setOnClickListener {
            if(data.attributes!!.image_link_url!=null && data.attributes!!.image_link_url!!.isNotEmpty()){
                val intent = Intent(context,WebView::class.java)
                intent.putExtra("url",data.attributes!!.image_link_url)
                context.startActivity(intent)
            }
        }
        container.addView(image)
        return image
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun getCount(): Int {
       return listData.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, mView: Any) {
        container.removeView(mView as View)
    }

    fun setData(newData : MutableList<BannerResponse.DataBean>){
        listData = newData
        notifyDataSetChanged()
    }
}