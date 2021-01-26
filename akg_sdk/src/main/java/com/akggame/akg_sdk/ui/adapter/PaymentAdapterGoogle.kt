package com.akggame.akg_sdk.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.PurchaseSDKCallback
import com.akggame.android.sdk.R
import com.android.billingclient.api.SkuDetails
import kotlinx.android.synthetic.main.item_list_product.view.*

class PaymentAdapterGoogle(
    val context: Context,
    private val purchaseSDKCallback: PurchaseSDKCallback
) :
    RecyclerView.Adapter<PaymentAdapterGoogle.ViewHolder>() {
    lateinit var view: View
    var skuDetails = mutableListOf<SkuDetails>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentAdapterGoogle.ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.item_list_product, null)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return skuDetails.size
    }

    override fun onBindViewHolder(holder: PaymentAdapterGoogle.ViewHolder, position: Int) {
        val data = skuDetails.get(position)
        holder.tvProductName.text = data.title
        holder.btnHarga.text = data.price
        holder.itemView.setOnClickListener {
            AKG_SDK.launchBilling(context as Activity, data, purchaseSDKCallback)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.tvProductName
        val btnHarga: TextView = itemView.btnHargaGameProduct

    }

    fun setInAppProduct(skuList: MutableList<SkuDetails>) {
        skuDetails = skuList
        notifyDataSetChanged()
    }

}