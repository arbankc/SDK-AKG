package com.akggame.newandroid

import android.app.Application
import com.orhanobut.hawk.Hawk


class AkgApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Hawk.init(this)
            .build()

    }

}




