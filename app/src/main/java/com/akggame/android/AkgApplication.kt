package com.akggame.android

import android.app.Application
import com.orhanobut.hawk.Hawk


class AkgApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(this)
            .build()

    }

}




