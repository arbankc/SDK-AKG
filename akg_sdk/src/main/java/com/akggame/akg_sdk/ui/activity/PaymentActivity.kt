package com.akggame.akg_sdk.ui.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout.VERTICAL
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akggame.akg_sdk.ui.adapter.PaymentAdapter
import kotlinx.android.synthetic.main.activity_payment.*
import com.google.android.gms.common.api.ApiException
import com.akggame.akg_sdk.dao.PaymentDao
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.reactivex.annotations.NonNull
import android.R
import android.app.Activity
import android.content.Intent
import android.util.Log
//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustEvent
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.ProductSDKCallback
import com.akggame.akg_sdk.PurchaseSDKCallback
import com.akggame.akg_sdk.dao.BillingDao
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.api.model.request.PostOrderRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.akggame.akg_sdk.dao.pojo.PurchaseItem
import com.akggame.akg_sdk.presenter.OrderPresenter
import com.akggame.akg_sdk.presenter.ProductPresenter
import com.akggame.akg_sdk.util.CacheUtil
import com.android.billingclient.api.*
import com.google.android.gms.wallet.*


class PaymentActivity : AppCompatActivity(),PurchaseSDKCallback {
    lateinit var mPaymentsClient: PaymentsClient
    lateinit var adapter: PaymentAdapter
    lateinit private var billingDao: BillingDao
    lateinit var productData: GameProductsResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.akggame.android.sdk.R.layout.activity_payment)
        adapter = PaymentAdapter(this,this)

        AKG_SDK.getProducts(application,this,object : ProductSDKCallback{
            override fun ProductResult(skuDetails: MutableList<SkuDetails>) {
                adapter.setInAppProduct(skuDetails)
            }
        })
    }

    override fun onPurchasedItem(purchaseItem: PurchaseItem) {
        setResult(Activity.RESULT_OK,intent.putExtra("orderDetail",purchaseItem))
        finish()
    }

    override fun onStart() {
        super.onStart()
        rvProduct.layoutManager = GridLayoutManager(this, 2)
        rvProduct.adapter = adapter
        rvProduct.setHasFixedSize(true)
    }
}
