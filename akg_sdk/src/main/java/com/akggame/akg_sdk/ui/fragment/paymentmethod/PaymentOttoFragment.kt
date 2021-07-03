package com.akggame.akg_sdk.ui.fragment.paymentmethod

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.akggame.akg_sdk.AKG_SDK.onSDKPayment
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.PAYMENT_TYPE
import com.akggame.akg_sdk.callback.PurchaseSDKCallback
import com.akggame.akg_sdk.dao.BillingDao
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.pojo.PurchaseItem
import com.akggame.akg_sdk.ui.activity.PaymentOttopayActivity
import com.akggame.akg_sdk.ui.adapter.PaymentAdapterGoogle
import com.akggame.akg_sdk.ui.dialog.menu.BindAccountDialog
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.newandroid.sdk.R
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.android.synthetic.main.fragment_payment_otto.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PaymentOttoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaymentOttoFragment : Fragment(),
    PurchaseSDKCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var productData: ProductData? = null
    var mView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_payment_otto, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        llPaymentGoogle.setOnClickListener {
            onSDKPayment(PAYMENT_TYPE.GOOGLE, activity as AppCompatActivity)
        }

        llOttoPayPayment.setOnClickListener {
            val bindAccountDialog = BindAccountDialog()
            if (CacheUtil.getPreferenceString(IConfig.LOGIN_TYPE, activity as Context)
                    ?.equals(IConfig.LOGIN_GUEST)!!
            ) {
                fragmentManager?.let { it1 -> bindAccountDialog.show(it1, "bind account") }
            } else {
                val intent = Intent(activity, PaymentOttopayActivity::class.java)
                intent.putExtra(Constants.DATA_GAME_PRODUCT, productData)
                startActivity(intent)
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(productDatas: ProductData, param2: String) =
            PaymentOttoFragment().apply {
                productData = productDatas
            }
    }

    override fun onPurchasedItem(purchaseItem: PurchaseItem) {
        activity?.setResult(
            Activity.RESULT_OK,
            activity!!.intent.putExtra("orderDetail", purchaseItem)
        )
        activity?.finish()
    }
}