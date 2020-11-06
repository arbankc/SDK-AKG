package com.akggame.akg_sdk.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.NonNull
import com.akggame.akg_sdk.IConfig
//import com.akggame.akg_sdk.ui.BaseActivity
import com.akggame.android.sdk.R

class LoadingDialogComponent(private val message: String, context: Context, style: Int) :
    Dialog(context, style) {
    private var text: TextView? = null
    private var widthLayout = WindowManager.LayoutParams.MATCH_PARENT
    private var heightLayout = WindowManager.LayoutParams.MATCH_PARENT

    protected override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setWindowContentDialogLayout(R.layout.dialog_loading)
        dialog = this

        text!!.text = message
    }

    companion object {

        private var dialog: LoadingDialogComponent? = null

//        @Synchronized
//        fun openLoadingDialog(activity: BaseActivity, message: String?): LoadingDialogComponent {
//            var message = message
//            if (dialog == null) {
//                message = message ?: IConfig.DEFAULT_LOADING
//
//                dialog = LoadingDialogComponent(message, activity, R.style.CoconutDialogFullScreen)
//                dialog!!.show()
//            }
//
//            return dialog as LoadingDialogComponent
//        }

//        fun closeLoadingDialog(ac: BaseActivity?) {
//            if (dialog == null) return
//            if (ac == null || !ac!!.isFinishing()) {
//                dialog!!.dismiss()
//                dialog = null
//            }
//        }
    }

    fun setWindowContentDialogLayout(layoutId: Int) {
        val w = window
        w!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(R.color.white)))
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        w.setLayout(widthLayout, heightLayout)
        val wlp = w.attributes
        wlp.gravity = Gravity.CENTER
        w.attributes = wlp

        setContentView(layoutId)

    }
}