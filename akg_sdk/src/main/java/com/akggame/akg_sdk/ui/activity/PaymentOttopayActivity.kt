package com.akggame.akg_sdk.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.webkit.WebView
import android.widget.Toast
import com.akggame.akg_sdk.dao.api.model.response.DepositResponse
import com.akggame.akg_sdk.presenter.OrderPresenter
import com.akggame.android.sdk.R
import com.clappingape.dplkagent.model.api.request.DepositRequest
import kotlinx.android.synthetic.main.activity_payment_ottopay.*

class PaymentOttopayActivity : AppCompatActivity(), OttopayIView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_ottopay)
        val data = DepositRequest(
            "feac7999-39b7-41c3-918a-6da632f355ca",
            "083b90c7-8f48-459d-815e-89b5d8cd58c9",
            "arif downline year",
            email = "arifdownyear@mail.com",
            phone = "081300000010",
            registered_number = "00000002",
            amount = 150000
        )
        OrderPresenter(this).onCreateDeposit(data,this)
    }

    override fun doOnSuccessCreateDeposit(data: DepositResponse) {
        loadUrl(data.data.url)
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
        paymentView.addJavascriptInterface(this, "Android")
        paymentView.requestFocus()
        paymentView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
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
            Toast.makeText(this,"Success payment",Toast.LENGTH_LONG).show()
        } else if (param.isNotEmpty() && param == "failed") {
            Toast.makeText(this,"Failed payment",Toast.LENGTH_LONG).show()
        }
    }
}