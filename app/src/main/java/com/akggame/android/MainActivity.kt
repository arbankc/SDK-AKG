package com.akggame.android

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.LoginSDKCallback
import com.akggame.akg_sdk.RelaunchSDKCallback
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


open class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
        Log.d("Density", resources.displayMetrics.widthPixels.toString())

        if (AKG_SDK.checkIsLogin(this)) {
            AKG_SDK.setRelauchDialog(this, object : RelaunchSDKCallback {
                override fun onContinue() {
                    Toast.makeText(this@MainActivity, "onContinue", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@MainActivity, Main2Activity::class.java))
                    finish()
                }

                override fun onReLogin() {
                    Toast.makeText(this@MainActivity, "onRelogin", Toast.LENGTH_LONG).show()
                    callLogin()
                }
            })
        } else {
            callLogin() //441
        }


    }

    private fun callLogin() {
        AKG_SDK.onLogin(this, object : LoginSDKCallback {
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
