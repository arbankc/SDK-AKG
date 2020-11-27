package com.akggame.akg_sdk.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.PurchaseSDKCallback
import com.akggame.akg_sdk.`interface`.OnClickItem
import com.akggame.akg_sdk.dao.BillingDao
import com.akggame.akg_sdk.dao.PaymentDao
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.akggame.akg_sdk.util.ConvertToRupiah
import com.akggame.android.sdk.R
import com.android.billingclient.api.SkuDetails
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.android.synthetic.main.content_dialog_verify.*
import kotlinx.android.synthetic.main.item_list_product.view.*

class PaymentAdapter(val context: Context) :
    RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {
    lateinit var view: View
    var skuDetails = mutableListOf<ProductData>()
    var onClickItem: OnClickItem? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.item_list_product, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return skuDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = skuDetails.get(position)
        holder.tvProductName.text = data.attributes?.game
        holder.btnTextHarga.text =
            ConvertToRupiah().toRupiah("Rp ", data.attributes?.price.toString(), false)
        holder.itemView.setOnClickListener {
            onClickItem?.clickItem(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.tvProductName
        val btnTextHarga: Button = itemView.btnHargaGameProduct
    }

    fun setInAppProduct(skuList: MutableList<ProductData>) {
        skuDetails = skuList
        notifyDataSetChanged()
    }

}