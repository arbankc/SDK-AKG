package com.akggame.akg_sdk.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.ProductSDKCallback
import com.akggame.akg_sdk.PurchaseSDKCallback
import com.akggame.akg_sdk.`interface`.OnClickItem
import com.akggame.akg_sdk.baseextend.BaseFragment
import com.akggame.akg_sdk.dao.BillingDao
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.akggame.akg_sdk.dao.pojo.PurchaseItem
import com.akggame.akg_sdk.presenter.ProductPresenter
import com.akggame.akg_sdk.ui.activity.FrameLayoutActivity
import com.akggame.akg_sdk.ui.activity.PaymentIView
import com.akggame.akg_sdk.ui.adapter.PaymentAdapter
import com.akggame.akg_sdk.ui.adapter.PaymentAdapterGoogle
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.android.sdk.R
import com.android.billingclient.api.SkuDetails
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.android.synthetic.main.activity_payment.*

class GameListProductFragment : BaseFragment(), PurchaseSDKCallback,
    PaymentIView {
    lateinit var mPaymentsClient: PaymentsClient
    lateinit var paymentAdapter: PaymentAdapter
    lateinit var paymentAdapterGoogle: PaymentAdapterGoogle

    lateinit private var billingDao: BillingDao
    lateinit var productData: GameProductsResponse
    var typePayment: String? = null
    var dialogChoicePayment: Dialog? = null
    var constraintLayout: ConstraintLayout? = null
    var progressBarPayment: ProgressBar? = null


    private val onClickItem: OnClickItem = object : OnClickItem {
        override fun clickItem(pos: Int) {
            val dataPaymentGameProduct = paymentAdapter.skuDetails[pos]
            val intent = Intent(activity, FrameLayoutActivity::class.java)
            intent.putExtra(Constants.DATA_GAME_PRODUCT, dataPaymentGameProduct.id.toString())
            intent.putExtra("pos", 3)
            activity?.startActivityForResult(intent, AKG_SDK.SDK_PAYMENT_CODE)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(getTypePayment: String, param2: String) =
            GameListProductFragment().apply {
                typePayment = getTypePayment
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_payment, container, false)

        progressBarPayment = view.findViewById(R.id.progressListPayment)
        constraintLayout = view.findViewById(R.id.clPayment)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        typePayment = intent.getStringExtra(Constants.TYPE_PAYMENT)

        paymentAdapterGoogle = PaymentAdapterGoogle(activity as Context, this)

        if (typePayment.equals("paymentgoogle", ignoreCase = true))
            initialListGoogleProduct()
        else if (typePayment.equals("paymentottopay", ignoreCase = true)) initialListProduct()


    }


    private fun initialListProduct() {
        initAdapter()

        dialogChoicePayment = activity?.let { Dialog(it) }
        dialogChoicePayment!!.setContentView(R.layout.layout_dialog_choice_payment)

        activity?.application?.let {
            ProductPresenter(this)
                .getProducts(
                    activity?.let { CacheUtil.getPreferenceString(IConfig.SESSION_GAME, it) },
                    activity as Context
                )
        }
    }

    fun initialListGoogleProduct() {
        AKG_SDK.getProductsGoogle(
            activity!!.application,
            activity as Context,
            object : ProductSDKCallback {
                override fun ProductResult(skuDetails: MutableList<SkuDetails>) {
                    paymentAdapterGoogle.setInAppProduct(skuDetails)

                    rvProduct.apply {
                        adapter = paymentAdapterGoogle
                        layoutManager = GridLayoutManager(activity, 2)
                        hasFixedSize()
                    }
                }
            })
    }

    private fun initAdapter() {
        paymentAdapter = PaymentAdapter(activity as Context)
        rvProduct.apply {
            adapter = paymentAdapter
            layoutManager = GridLayoutManager(activity, 2)
            hasFixedSize()
        }
        paymentAdapter.onClickItem = onClickItem
    }

    override fun onPurchasedItem(purchaseItem: PurchaseItem) {
        activity?.setResult(
            Activity.RESULT_OK,
            activity?.intent?.putExtra("orderDetail", purchaseItem)
        )
        activity?.finish()

    }

    override fun doOnError(message: String) {

    }

    override fun doOnSuccessPost(o: GameProductsResponse) {
        paymentAdapter.setInAppProduct(o.data.toMutableList())
    }

    override fun doShowProgress(isShow: Boolean) {
        if (isShow) {
            progressBarPayment?.let { showView(it) }
            constraintLayout?.let { hideView(it) }
        } else {
            progressBarPayment?.let { hideView(it) }
            constraintLayout?.let { showView(it) }
        }
    }

    override fun handleError(message: String) {
    }

    override fun handleRetryConnection() {
    }

}
