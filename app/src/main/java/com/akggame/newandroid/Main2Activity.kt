package com.akggame.newandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.akggame.akg_sdk.*
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.callback.EulaSdkCallBack
import com.akggame.akg_sdk.callback.LogoutSdkCallback
import com.akggame.akg_sdk.callback.MenuSDKCallback
import com.akggame.akg_sdk.dao.api.model.response.EulaResponse
import com.akggame.akg_sdk.dao.pojo.PurchaseItem
import com.akggame.akg_sdk.util.Constants
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : BaseActivity(),
    MenuSDKCallback {
    var saveBanner: String? = null

    override fun onCheckSDK(isUpdated: Boolean) {
        AKG_SDK.setCallCheckSdkDialog(supportFragmentManager)
    }

    override fun onClickEula(context: Context, idGame: String) {
        AKG_SDK.callEulaWebview(
            this,
            idGame,
            object : EulaSdkCallBack { // calling webview with api
                override fun onSuccesEula(eulaReponse: EulaResponse) {
                    if (eulaReponse.meta?.code == 404) {
                        showToast(eulaReponse.data?.message)
                    } else {
                        AKG_SDK.callEula(
                            this@Main2Activity,
                            eulaReponse.data?.attributes?.url.toString()
                        ) //call webview with url
                    }
                }

                override fun onErrorEula(message: String) {

                }

            })
    }

    override fun onClickFbPage(context: Context) {
        AKG_SDK.callFbFanPage(this, "https://www.facebook.com/akggames/") //calling webview with url
    }

    override fun onContactUs(context: Context) {
        AKG_SDK.setCallContactDialog(supportFragmentManager)
    }

    override fun onLogout() {
        AKG_SDK.setCallLogoutDialog(supportFragmentManager, object :
            LogoutSdkCallback {
            override fun onSuccesLogout() {
                intentPageFlags(MainActivity::class.java)
            }
        })
    }

    override fun onBindAccount(context: Context) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        //setfloating button
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

//        AKG_SDK.callStartGame(supportFragmentManager, object : StartGameSDKCallback {
//            override fun onStartGame(dataItemGameList: DataItemGameList) {
//                showToast("get data item list ${dataItemGameList.attributes?.name}")
//            }
//        })

    }

    override fun onStop() {
        super.onStop()
        AKG_SDK.callSessionStop(this)
    }

    override fun onStart() {
        super.onStart()
        AKG_SDK.callSessionStart(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AKG_SDK.callSessionStop(this)
    }

    private fun callBanner() {
        //callBanner
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
