package com.akggame.akg_sdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.akggame.akg_sdk.dao.AkgDao
import com.akggame.akg_sdk.ui.activity.PaymentActivity
import com.akggame.akg_sdk.ui.component.FloatingButton
import com.akggame.akg_sdk.util.CacheUtil
import com.android.billingclient.api.SkuDetails
import com.akggame.akg_sdk.dao.*
import com.akggame.akg_sdk.ui.activity.PaymentOttopayActivity

enum class PAYMENT_TYPE {
    GOOGLE, OTTOPAY
}

object AKG_SDK {

    private lateinit var menuCallback: MenuSDKCallback

    //    lateinit var activity: AppCompatActivity
    private lateinit var mFloatingButton: FloatingButton
    private val AkgDao = AkgDao()
    const val SDK_PAYMENT_CODE = 199
    const val SDK_PAYMENT_DATA = "akg_purchase_data"
    const val LOGIN_GOOGLE = "loginGoogle"
    const val LOGIN_FACEBOOK = "loginFacebook"
    const val LOGIN_PHONE = "loginPhone"

    @JvmStatic
    fun checkIsLogin(context: Context): Boolean {
        return CacheUtil.getPreferenceBoolean(IConfig.SESSION_LOGIN, context)
    }

    @JvmStatic
    fun getMenuCallback(): MenuSDKCallback {
        return menuCallback
    }

    @JvmStatic
    fun registerAdjustOnAKG(gameProvider: String, application: Application) {
//        AkgDao.registerAdjust(gameProvider, application)
        CacheUtil.putPreferenceString(IConfig.SESSION_GAME, gameProvider, application)

    }

    @JvmStatic
    fun getProducts(application: Application, context: Context, callback: ProductSDKCallback) {
        AkgDao.getProducts(application, context, callback)
    }

    @JvmStatic
    fun callBannerDialog(activity: AppCompatActivity) {
        AkgDao.callBannerDialog(activity)
    }

    @JvmStatic
    fun launchBilling(activity: Activity, skuDetails: SkuDetails, callback: PurchaseSDKCallback) {
        AkgDao.launchBilling(activity, skuDetails, callback)
    }

    @JvmStatic
    fun setRelauchDialog(activity: AppCompatActivity, callback: RelaunchSDKCallback) {
        AkgDao.callRelaunchDialog(activity, callback)
    }

    @JvmStatic
    fun resetFloatingButton(activity: AppCompatActivity) {
        setFloatingButton(activity, mFloatingButton, activity as Context, menuCallback)
    }

    @JvmStatic
    fun setFloatingButton(
        activity: AppCompatActivity,
        floatingButton: FloatingButton,
        context: Context,
        menuSDKCallback: MenuSDKCallback
    ) {
        menuCallback = menuSDKCallback
        AkgDao.setFloatingButtonListener(activity, floatingButton, context, menuSDKCallback)
        mFloatingButton = AkgDao.setFloatingButtonItem(floatingButton, activity)
    }

    @JvmStatic
    fun onLogin(activity: AppCompatActivity, gameName: String, loginSDKCallback: LoginSDKCallback) {
        AkgDao.callLoginDialog(activity, gameName, loginSDKCallback)
    }

    @JvmStatic
    fun onSDKPayment(paymentType: PAYMENT_TYPE, activity: AppCompatActivity) {
        when (paymentType) {
            PAYMENT_TYPE.GOOGLE -> {
                val intent = Intent(activity, PaymentActivity::class.java)
                activity.startActivityForResult(intent, SDK_PAYMENT_CODE)
            }
            PAYMENT_TYPE.OTTOPAY -> {
                val intent = Intent(activity, PaymentOttopayActivity::class.java)
                activity.startActivityForResult(intent, SDK_PAYMENT_CODE)
            }
        }

    }
}