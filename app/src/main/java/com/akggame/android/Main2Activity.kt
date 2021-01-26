package com.akggame.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.EulaSdkCallBack
import com.akggame.akg_sdk.MenuSDKCallback
import com.akggame.akg_sdk.PAYMENT_TYPE
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.dao.api.model.response.CurrentUserResponse
import com.akggame.akg_sdk.dao.api.model.response.EulaResponse
import com.akggame.akg_sdk.dao.pojo.PurchaseItem
import com.akggame.akg_sdk.ui.activity.eula.EulaIView
import com.akggame.akg_sdk.util.Constants
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : BaseActivity(), MenuSDKCallback {
    var saveBanner: String? = null

    override fun onCheckSDK(isUpdated: Boolean) {

    }

    override fun onClickEula(context: Context, idGame: String) {
        AKG_SDK.callEulaWebview(
            this,
            "6122bb1e-e189-4b0d-bfbe-cb2c4180d878",
            object : EulaSdkCallBack { // calling webview with api
                override fun onSuccesEula(eulaReponse: EulaResponse) {
                    Log.d("Respon url", " ${eulaReponse.data?.attributes?.url}")
                    AKG_SDK.callEula(
                        this@Main2Activity,
                        eulaReponse.data?.attributes?.url.toString()
                    ) //call webview with url
                }

                override fun onErrorEula(message: String) {

                }

            })

    }

    override fun onClickFbPage(context: Context) {
        AKG_SDK.callFbFanPage(this, "https://www.facebook.com/akggames/") //calling webview with url
    }

    override fun onContactUs(context: Context) {

    }

    override fun onLogout() {
        startActivity(Intent(this@Main2Activity, MainActivity::class.java))
        finish()
    }

    override fun onBindAccount(context: Context) {

    }

    val displayMetrics = DisplayMetrics();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        AKG_SDK.setFloatingButton(this, floatingButton, this, this)
        floatingButton.setFloat()

        saveBanner = Hawk.get("callBanner")
        if (!saveBanner.equals("true"))
            callBanner()

        btnPaymentGoogle.setOnClickListener {
            AKG_SDK.onSDKPayment(PAYMENT_TYPE.GOOGLE, this)
        }

        btnPaymentOttoPay.setOnClickListener {
            AKG_SDK.onSDKPayment(PAYMENT_TYPE.OTTOPAY, this)
        }

    }

    override fun onStart() {
        super.onStart()

        val bundle = Bundle()
        bundle.putString("timestamp", createTimestamp())
        bundle.putString("uid", getDataLogin()?.data?.id)
        bundle.putString("udid", deviceIdAndroid())

        hitEventFirebase("session_start", bundle)
    }

    override fun onStop() {
        super.onStop()

        val bundle = Bundle()
        bundle.putString("timestamp", createTimestamp())
        bundle.putString("uid", getDataLogin()?.data?.id)
        bundle.putString("udid", deviceIdAndroid())
        hitEventFirebase("session_stop", bundle)
    }

    override fun onDestroy() {
        super.onDestroy()

        val bundle = Bundle()
        bundle.putString("timestamp", createTimestamp())
        bundle.putString("uid", getDataLogin()?.data?.id)
        bundle.putString("udid", deviceIdAndroid())

        hitEventFirebase("session_stop", bundle)
    }

    private fun callBanner() {
        AKG_SDK.callBannerDialog(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AKG_SDK.SDK_PAYMENT_CODE) {
                val payment: PurchaseItem? = data?.getParcelableExtra("orderDetail")
                Toast.makeText(this, payment?.product_name, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Hawk.delete("callBanner")
    }
}
