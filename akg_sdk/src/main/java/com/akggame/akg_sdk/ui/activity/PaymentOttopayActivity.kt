package com.akggame.akg_sdk.ui.activity

import android.os.Bundle
import android.webkit.*
import android.webkit.WebView
import android.widget.Toast
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.callback.StatusOttoPayCallback
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.api.model.response.DepositResponse
import com.akggame.akg_sdk.presenter.OrderPresenter
import com.akggame.akg_sdk.util.Constants
import com.akggame.akg_sdk.util.JSBridge
import com.akggame.newandroid.sdk.R
import com.clappingape.dplkagent.model.api.request.DepositRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_payment_ottopay.*

class PaymentOttopayActivity : BaseActivity(), OttopayIView, StatusOttoPayCallback {
    var productData: ProductData? = null
    val data = DepositRequest()
    var firebaseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_ottopay)
        initial()
    }

    private fun initial() {
        productData = intent.getParcelableExtra(Constants.DATA_GAME_PRODUCT)
        data.user_id = getDataLogin()?.data?.id.toString()
        data.game_product_id = productData?.id.toString()

        OrderPresenter(this)
            .onCreateDeposit(data, this)
    }


    override fun doOnSuccessCreateDeposit(data: DepositResponse) {
        val bundle = Bundle()
        bundle.putString("User Id", data.data.id)
        loadUrl(data.data.endpoint_url)

    }

    override fun handleError(message: String) {
    }

    override fun handleRetryConnection() {
    }

    private fun loadUrl(url: String) {
        val MyUA =
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36  (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
        paymentView.settings.javaScriptEnabled = true
        paymentView.settings.userAgentString = MyUA
        paymentView.settings.domStorageEnabled = true
        paymentView.isVerticalScrollBarEnabled = true
        paymentView.requestFocus()
        firebaseId = Hawk.get<String>(Constants.FIREBASE_ID)

        val jsBridge = JSBridge(
            this,
            this
        )

        paymentView.addJavascriptInterface(jsBridge, "Android")
        paymentView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url.toString())
                return true
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {

            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {

            }
        }
        paymentView.loadUrl(url)
    }

    @JavascriptInterface
    fun closeWindow(param: String) {
        if (param.isNotEmpty() && param == "success") {
            Toast.makeText(this, "Success payment", Toast.LENGTH_LONG).show()
        } else if (param.isNotEmpty() && param == "failed") {
            Toast.makeText(this, "Failed payment", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSuccess(status: String) {
        val bundle = Bundle()
        bundle.putString("uid", firebaseId)
        bundle.putString("item_id", productData?.attributes?.game_id.toString())
        bundle.putString("amount", productData?.attributes?.price)
        bundle.putString("timestamp", createTimestamp())
        bundle.putString("channel", "ottopay")
        bundle.putString("status", "success")
        println("respon failed ottopay $status")
        hitEventFirebase(Constants.PURCHASE_SUKSES, bundle)
        FirebaseAnalytics.getInstance(this).logEvent(status, bundle)
    }

    override fun onFailed(status: String) {
        val bundle = Bundle()
        bundle.putString("uid", firebaseId)
        bundle.putString("item_id", productData?.attributes?.game_id.toString())
        bundle.putString("amount", productData?.attributes?.price)
        bundle.putString("timestamp", createTimestamp())
        bundle.putString("channel", "ottopay")
        bundle.putString("status", "failed")
        hitEventFirebase(Constants.PURCHASE_FAILED, bundle)
        FirebaseAnalytics.getInstance(this).logEvent(status, bundle)
        println("respon failed ottopay $status")
    }
}