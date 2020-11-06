package com.akggame.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.MenuSDKCallback
import com.akggame.akg_sdk.PAYMENT_TYPE
import com.akggame.akg_sdk.dao.pojo.PurchaseItem
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity(),MenuSDKCallback {
    override fun onSuccessBind(token: String, loginType: String) {

    }

    override fun onCheckSDK(isUpdated: Boolean) {

    }
    override fun onLogout() {
        startActivity(Intent(this@Main2Activity,MainActivity::class.java))
        finish()
    }

    val displayMetrics = DisplayMetrics();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        AKG_SDK.setFloatingButton(this,floatingButton, this,this)
        floatingButton.setFloat()
        callBanner()

        btnPayment.setOnClickListener {
            AKG_SDK.onSDKPayment(PAYMENT_TYPE.GOOGLE,this)
        }

        btnPaymentOttoPay.setOnClickListener {
            AKG_SDK.onSDKPayment(PAYMENT_TYPE.OTTOPAY,this)
        }
    }

    fun callBanner(){
        AKG_SDK.callBannerDialog(this)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode == AKG_SDK.SDK_PAYMENT_CODE){
                val payment : PurchaseItem? = data?.getParcelableExtra("orderDetail")
                Toast.makeText(this,payment?.product_name,Toast.LENGTH_LONG).show()
            }
        }
    }
}
