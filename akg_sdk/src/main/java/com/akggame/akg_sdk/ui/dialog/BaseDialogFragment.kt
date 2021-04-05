package com.akggame.akg_sdk.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.newandroid.sdk.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk


open class BaseDialogFragment : DialogFragment(), IView {

    var dialogLoading: Dialog? = null

    interface JSONConvertable {
        fun toJSON(): String = Gson().toJson(this)
    }

    inline fun <reified T : JSONConvertable> String.toObject(): T =
        Gson().fromJson(this, T::class.java)

    override fun handleError(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    var myFragmentManager: FragmentManager? = null
    var dialogCustomlist: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val style = STYLE_NO_FRAME
        setStyle(style, theme)

        dialogCustomlist = activity?.let { Dialog(it) }
        dialogLoading = activity?.let { Dialog(it) }
        dialogLoading?.setContentView(R.layout.dialog_loading)

    }

    fun deleteLogin() {
        activity!!.finish()
        Hawk.deleteAll()
        CacheUtil.clearPreference(activity!!)
    }

    fun createTimestamp(): String {
        val tsLong = System.currentTimeMillis() / 1000
        return tsLong.toString()
    }

    fun getAccessToken(): String {
        return Hawk.get<String>(IConfig.SESSION_TOKEN)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            this.dialog?.window?.setLayout(width, height)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.dialog?.window?.setBackgroundDrawable(
                    ColorDrawable(
                        context?.resources!!.getColor(
                            R.color.transparent,
                            null
                        )
                    )
                )
            }
        }
        setOnBackPressed()

    }


    fun hitEventFirebase(eventName: String, bundle: Bundle) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun showDialogLoading(isShow: Boolean) {
        if (isShow) dialogLoading?.show()
        else dialogLoading?.dismiss()
    }

    fun dialogCustom(): Dialog {
        dialogCustomlist?.setContentView(R.layout.layout_dialog_customlist)
        dialogCustomlist?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        return dialogCustomlist!!
    }

    fun showToast(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    open fun setOnBackPressed() {
        dialog?.setOnKeyListener(object : View.OnKeyListener, DialogInterface.OnKeyListener {
            override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
                return false
            }

            override fun onKey(p0: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    onBackPressed()
                    return true
                } else {
                    return false
                }
            }
        })
    }


    override fun handleRetryConnection() {
    }

    fun customDismiss() {
        val ft = myFragmentManager!!.beginTransaction()
        ft.remove(this)
        ft.commit()
    }

    fun clearBackStack() {
        val count = myFragmentManager?.backStackEntryCount!!
        for (i in 0 until count) {
            myFragmentManager?.popBackStack()
        }
    }

    fun clearBackStackNew(fragmentManager: FragmentManager) {
        val count = fragmentManager.backStackEntryCount
        for (i in 0 until count) {
            fragmentManager.popBackStack()
        }
    }

    fun restartBackStack() {
        customDismiss()
        val backStackSize = myFragmentManager?.backStackEntryCount
        if (backStackSize != null) {
            if (backStackSize > 0) {
                val backEntry = myFragmentManager?.getBackStackEntryAt(0)
                val count = myFragmentManager?.backStackEntryCount!!
                if (count > 0) {
                    for (i in 0 until count - 1) {
                        myFragmentManager?.popBackStack()
                    }
                }
                val mDialog =
                    myFragmentManager?.findFragmentByTag(backEntry?.name) as BaseDialogFragment
                if (myFragmentManager != null) {
//                    myFragmentManager!!.beginTransaction().remove(mDialog)
                    mDialog.show(myFragmentManager!!.beginTransaction(), null)
                }
            }
        }
    }

    fun onRelauch() {
        clearBackStack()
    }

    fun onBackPressed() {
        val backStackSize = myFragmentManager?.backStackEntryCount

        if (backStackSize != null) {
            if (backStackSize > 1) {
                val backEntry = myFragmentManager?.getBackStackEntryAt(backStackSize - 2)
                customDismiss()
                myFragmentManager?.popBackStack(this.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val mDialog =
                    myFragmentManager?.findFragmentByTag(backEntry?.name) as BaseDialogFragment
                if (myFragmentManager != null) {
                    myFragmentManager!!.beginTransaction().remove(mDialog)
                    mDialog.show(myFragmentManager!!.beginTransaction(), null)
                }
            }
        }
    }

    fun showLastFragment() {
        val backStackSize = myFragmentManager?.backStackEntryCount
        if (backStackSize != null) {
            if (backStackSize > 0) {
                val backEntry = myFragmentManager?.getBackStackEntryAt(backStackSize - 1)
                val mDialog =
                    myFragmentManager?.findFragmentByTag(backEntry?.name) as BaseDialogFragment
                if (myFragmentManager != null) {
                    myFragmentManager!!.beginTransaction().remove(mDialog)
                    mDialog.show(myFragmentManager!!.beginTransaction(), null)
                }
            }
        }
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        onRelauch()
//    }

    fun getModelHp(): String {
        return Build.MODEL
    }

    fun getManufacturHp(): String {
        return Build.MANUFACTURER
    }

    fun getVersionOsHp(): Int {
        return Build.VERSION.SDK_INT
    }
}