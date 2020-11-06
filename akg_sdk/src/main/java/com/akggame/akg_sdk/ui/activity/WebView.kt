package com.akggame.akg_sdk.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        wvInfo.loadUrl(intent.getStringExtra("url"))
    }
}
