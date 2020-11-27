package com.akggame.android

import android.app.Application
import com.orhanobut.hawk.Hawk


class AkgApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        AKG_SDK.registerAdjustOnAKG("mobile-legend", this)

        Hawk.init(this)
            .build()

    }

}




