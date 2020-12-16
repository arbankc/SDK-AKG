package com.akggame.akg_sdk.ui.activity

import android.os.Bundle
import android.webkit.*
import android.webkit.WebView
import android.widget.Toast
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.dao.api.model.response.DepositResponse
import com.akggame.akg_sdk.presenter.OrderPresenter
import com.akggame.akg_sdk.util.Constants
import com.akggame.android.sdk.R
import com.clappingape.dplkagent.model.api.request.DepositRequest
import kotlinx.android.synthetic.main.activity_payment_ottopay.*

class PaymentOttopayActivity : BaseActivity(), OttopayIView {
    var idProductGame: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_ottopay)

        initial()

    }

    private fun initial() {
        idProductGame = intent.getStringExtra(Constants.DATA_GAME_PRODUCT)

        val data = DepositRequest()
        data.user_id = getDataLogin()?.data?.id.toString()
        data.game_product_id = idProductGame.toString()

        OrderPresenter(this)
            .onCreateDeposit(data, this)
    }


    override fun doOnSuccessCreateDeposit(data: DepositResponse) {
        val bundle = Bundle()
        bundle.putString("User Id", data.data.id)


//        bundle.putString("Game Product Id", data.data.)

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
        paymentView.addJavascriptInterface(this, "Android")
        paymentView.requestFocus()

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
}