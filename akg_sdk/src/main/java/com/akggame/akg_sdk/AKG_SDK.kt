package com.akggame.akg_sdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.dao.AkgDao
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.presenter.LoginPresenter
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.ui.activity.FrameLayoutActivity
import com.akggame.akg_sdk.ui.activity.eula.EulaIView
import com.akggame.akg_sdk.ui.component.FloatingButton
import com.akggame.akg_sdk.ui.dialog.GameListDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.android.billingclient.api.SkuDetails

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

    var startGameSDKCallback: StartGameSDKCallback? = null
    var eulaIView: EulaIView? = null

    @JvmStatic
    fun checkIsLogin(context: Context): Boolean {
        return CacheUtil.getPreferenceBoolean(IConfig.SESSION_LOGIN, context)
    }

    @JvmStatic
    fun getMenuCallback(): MenuSDKCallback {
        return menuCallback
    }

    @JvmStatic
    fun getStartSdkCallback(): StartGameSDKCallback? {
        return startGameSDKCallback
    }

    @JvmStatic
    fun registerAdjustOnAKG(gameProvider: String, application: Application) {
//        AkgDao.registerAdjust(gameProvider, application)
//        CacheUtil.putPreferenceString(IConfig.SESSION_GAME, gameProvider, application)

    }

    @JvmStatic
    fun getProducts(application: Application, context: Context, callback: ProductSDKCallback) {
        AkgDao.getProducts(application, context, callback)
    }


    @JvmStatic
    fun getProductsGoogle(
        application: Application,
        context: Context,
        callback: ProductSDKCallback
    ) {
        AkgDao.getProductsGoogle(application, context, callback)
    }

    @JvmStatic
    fun callBannerDialog(activity: AppCompatActivity) {
        AkgDao.callBannerDialog(activity)
    }

    @JvmStatic
    fun callEulaWebview(
        activity: AppCompatActivity,
        idGame: String,
        eulaSdkCallBack: EulaSdkCallBack
    ) {
        AkgDao.callApiWebviewEula(activity, idGame, eulaSdkCallBack)
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
    fun setCallContactDialog(fragmentManager: FragmentManager) {
        AkgDao.callContactDialog(fragmentManager)
    }


    @JvmStatic
    fun setCallCheckSdkDialog(fragmentManager: FragmentManager) {
        AkgDao.callCheckVersionDialog(fragmentManager)
    }

    @JvmStatic
    fun setCallLogoutDialog(fragmentManager: FragmentManager, menuSDKCallback: LogoutSdkCallback) {
        AkgDao.callLogoutDialog(menuSDKCallback, fragmentManager)
    }

    @JvmStatic
    fun callFbFanPage(activity: AppCompatActivity, url: String) {
        AkgDao.callBrowserFanPage(activity, url)
    }

    @JvmStatic
    fun callEula(activity: AppCompatActivity, url: String) {
        AkgDao.callDetailEula(activity, url)
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
    fun onLogin(activity: AppCompatActivity, loginSDKCallback: LoginSDKCallback) {
        AkgDao.callLoginDialog(activity, loginSDKCallback)
    }

    @JvmStatic
    fun onPaymentOttoPay(
        activity: AppCompatActivity,
        userId: String,
        idProductGame: String,
        ottoPaySDKCallback: OttoPaySDKCallback
    ) {
        AkgDao.callPaymentOttoPay(activity, userId, idProductGame, ottoPaySDKCallback)
    }

    @JvmStatic
    fun onSDKPayment(paymentType: PAYMENT_TYPE, activity: AppCompatActivity) {
        when (paymentType) {
            PAYMENT_TYPE.GOOGLE -> {
                val intent = Intent(activity, FrameLayoutActivity::class.java)
                intent.putExtra(Constants.TYPE_PAYMENT, "paymentgoogle")
                intent.putExtra("pos", 1)
                activity.startActivityForResult(intent, SDK_PAYMENT_CODE)
            }
            PAYMENT_TYPE.OTTOPAY -> {
                val intent = Intent(activity, FrameLayoutActivity::class.java)
                intent.putExtra(Constants.TYPE_PAYMENT, "paymentottopay")
                intent.putExtra("pos", 1)
                activity.startActivityForResult(intent, SDK_PAYMENT_CODE)

            }
        }

    }

    @JvmStatic
    fun callStartUpsert(
        mIView: IView,
        context: Context,
        facebookAuthRequest: FacebookAuthRequest,
        typeLogin: String,
        startSDK: StartSDKCallback
    ) {
        LoginPresenter(mIView)
            .upsertUser(facebookAuthRequest, context, typeLogin)
    }

    @JvmStatic
    fun callStartGame(fragmentManager: FragmentManager, startGame: StartGameSDKCallback) {
        startGameSDKCallback = startGame
        val banner = GameListDialogFragment()
        val ftransaction = fragmentManager.beginTransaction()
        ftransaction.addToBackStack("gamedialog")
        banner.show(ftransaction, "gamedialog")
    }


    @JvmStatic
    fun callListGame(context: Context, iView: IView) {
        AkgDao.callListGame(iView, context)
    }

}