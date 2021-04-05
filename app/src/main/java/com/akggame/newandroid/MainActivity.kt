package com.akggame.newandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.callback.LoginSDKCallback
import com.akggame.akg_sdk.callback.RelaunchSDKCallback
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric


open class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
        Log.d("Density", resources.displayMetrics.widthPixels.toString())
        if (AKG_SDK.checkIsLogin(this)) {
            AKG_SDK.setRelauchDialog(this, object :
                RelaunchSDKCallback {
                override fun onContinue() {
                    Toast.makeText(this@MainActivity, "onContinue", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@MainActivity, Main2Activity::class.java))
                    finish()
                }

                override fun onReLogin() {
                    Toast.makeText(this@MainActivity, "onRelogin", Toast.LENGTH_LONG).show()
                    intentPageFlags(MainActivity::class.java)
                }
            })
        } else {
            callLogin() //441
        }

        println("respon Vesion ${getVersionOsHp()} and ${getModelHp()} manufatur ${getManufacturHp()}")
    }

    private fun callLogin() {
        AKG_SDK.onLogin(this, object :
            LoginSDKCallback {
            override fun onResponseSuccess(token: String, username: String, loginType: String) {
                Toast.makeText(this@MainActivity, "Success Login $username", Toast.LENGTH_LONG)
                    .show()
                startActivity(Intent(this@MainActivity, Main2Activity::class.java))
                finish()
            }

            override fun onResponseFailed(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }
        })
    }
}
