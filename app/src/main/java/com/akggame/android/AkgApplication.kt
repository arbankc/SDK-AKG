package com.akggame.android

import android.app.Application
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.akggame.akg_sdk.AKG_SDK


    class AkgApplication : Application() {

        override fun onCreate() {
            super.onCreate()
            AKG_SDK.registerAdjustOnAKG("mobile-legend",this)
        }

    }




