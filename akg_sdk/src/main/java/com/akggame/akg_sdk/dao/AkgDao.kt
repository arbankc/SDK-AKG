package com.akggame.akg_sdk.dao

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.callback.*
import com.akggame.akg_sdk.dao.api.model.FloatingItem
import com.akggame.akg_sdk.dao.api.model.response.CurrentUserResponse
import com.akggame.akg_sdk.dao.api.model.response.DepositResponse
import com.akggame.akg_sdk.dao.api.model.response.EulaResponse
import com.akggame.akg_sdk.presenter.*
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.ui.activity.OttopayIView
import com.akggame.akg_sdk.ui.activity.eula.EulaIView
import com.akggame.akg_sdk.ui.adapter.FloatingAdapterListener
import com.akggame.akg_sdk.ui.component.FloatingButton
import com.akggame.akg_sdk.ui.dialog.banner.BannerDialog
import com.akggame.akg_sdk.ui.dialog.login.LoginDialogFragment
import com.akggame.akg_sdk.ui.dialog.login.RelaunchDialog
import com.akggame.akg_sdk.ui.dialog.menu.*
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.akg_sdk.util.JSBridge
import com.akggame.newandroid.sdk.R
import com.android.billingclient.api.SkuDetails
import com.clappingape.dplkagent.model.api.request.DepositRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.orhanobut.hawk.Hawk


class AkgDao : AccountIView, OttopayIView, EulaIView {

    private lateinit var customCallback: LoginSDKCallback
    private val productPresenter = ProductPresenter(this)
    private val eulaPresenter = EulaPresenter(this)
    private val presenter = InfoPresenter(this)
    lateinit var ottoPaySDKCallback: OttoPaySDKCallback
    lateinit var eulaSdkCallBack: EulaSdkCallBack

    var idUser: String? = null

    fun callRelaunchDialog(activity: AppCompatActivity, callback: RelaunchSDKCallback) {
        val dialog = RelaunchDialog.newInstance(callback)
        val ftransaction = activity.supportFragmentManager.beginTransaction()
        ftransaction.addToBackStack("relaunch")
        dialog.show(ftransaction, "relaunch")
    }

    fun registerAdjust(gameProvider: String, application: Application) {
        presenter.onGetSDKConf(gameProvider, application, application)
    }

    fun getProducts(application: Application, context: Context, callback: ProductSDKCallback) {
        productPresenter.getProducts(
            CacheUtil.getPreferenceString(IConfig.SESSION_GAME, context),
            context
        )
    }

    fun getProductsGoogle(
        application: Application,
        context: Context,
        callback: ProductSDKCallback
    ) {
        productPresenter.getProductsGoogle(
            application, callback
        )
    }

    fun callBrowserFanPage(context: Context, urlString: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(urlString)
        context.startActivity(i)
    }

    fun launchBilling(activity: Activity, skuDetails: SkuDetails, callback: PurchaseSDKCallback) {
        productPresenter.lauchBilling(activity, skuDetails, callback)
    }

    fun callBannerDialog(activity: AppCompatActivity) {
        val banner = BannerDialog()
        val ftransaction = activity.supportFragmentManager.beginTransaction()
        ftransaction.addToBackStack("banner")
        banner.show(ftransaction, "banner")
    }

    fun getDataLogin(): CurrentUserResponse? {
        return Hawk.get(Constants.DATA_LOGIN)
    }

    fun deviceIdAndroid(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ANDROID_ID
        )
    }

    fun hitEventFirebase(eventName: String, bundle: Bundle, context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun callSessionStart(context: Context) {
        val packageName = Hawk.get<String>(Constants.ID_GAME_PROVIDER)
        val bundle = Bundle()
        bundle.putString("game_provider", packageName)
        bundle.putString("uid", getDataLogin()?.data?.attributes?.firebase_id)
        bundle.putString("udid", deviceIdAndroid(context))
        hitEventFirebase(Constants.SESSION_START, bundle, context)

    }

    fun callSessionStop(context: Context) {
        val packageName = Hawk.get<String>(Constants.ID_GAME_PROVIDER)
        val bundle = Bundle()
        bundle.putString("game_provider", packageName)
        bundle.putString("uid", getDataLogin()?.data?.attributes?.firebase_id)
        bundle.putString("udid", deviceIdAndroid(context))
        hitEventFirebase(Constants.SESSION_STOP, bundle, context)
    }


    fun getStatusOttopay(
        activity: AppCompatActivity,
        statusOttoPayCallback: StatusOttoPayCallback
    ) {
        JSBridge(activity, statusOttoPayCallback)
    }

    fun callApiWebviewEula(context: Context, idGame: String, eulaSdkCallBackEula: EulaSdkCallBack) {
        eulaSdkCallBack = eulaSdkCallBackEula
        eulaPresenter.onGetEula(context, idGame)
    }

    fun setFloatingButtonListener(
        activity: AppCompatActivity,
        floatingButton: FloatingButton,
        context: Context,
        menuSDKCallback: MenuSDKCallback
    ) {
        val onItemClickListener: FloatingAdapterListener = object : FloatingAdapterListener {
            override fun onItemClick(position: Int, floatingItem: FloatingItem) {
                val idGame = Hawk.get<String>(Constants.ID_GAME)
                val accountDialog = AccountDialog.newInstance(activity.supportFragmentManager)
                val bindAccountDialog = BindAccountDialog.newInstance(
                    activity.supportFragmentManager,
                    floatingButton,
                    menuSDKCallback
                )
                when (position) {
                    0 -> {
                        if (CacheUtil.getPreferenceString(IConfig.LOGIN_TYPE, activity)
                                ?.equals(IConfig.LOGIN_GUEST)!!
                        ) {
                            bindAccountDialog.show(activity.supportFragmentManager, "bind account")
                        } else {
                            accountDialog.show(activity.supportFragmentManager, "account")
                        }
                    }
                    1 -> {
//                        callBrowserFanPage(context)
                        menuSDKCallback.onClickFbPage(context)
                    }
                    2 -> {
//                        callDetailEula(context)
                        menuSDKCallback.onClickEula(context, idGame)
                    }

                    3 -> {
//                        callContactDialog(activity.supportFragmentManager)
                        menuSDKCallback.onContactUs(context)
                    }

                    4 -> {
//                        callCheckVersionDialog(activity.supportFragmentManager)
                        menuSDKCallback.onCheckSDK(true)
                    }
                    5 -> {
                        menuSDKCallback.onLogout()
                    }
                    6 -> {
                        hitEventLevelup(context)
                    }
                }
            }
        }

        floatingButton.floatingAdapterListener = onItemClickListener
    }

    fun hitEventLevelup(context: Context) {
        val packageName = Hawk.get<String>(Constants.ID_GAME_PROVIDER)
        val bundle = Bundle()
        bundle.putInt("level", 10000)
        bundle.putString("game_provider", packageName)
        bundle.putString("uid", getDataLogin()?.data?.attributes?.firebase_id)
        hitEventFirebase("level_up", bundle, context)
    }

    fun callLogoutDialog(menuSDKCallback: LogoutSdkCallback, fragmentManager: FragmentManager) {
        val logoutDialog = LogoutDialog.newInstance(menuSDKCallback)
        logoutDialog.show(fragmentManager, "logout")
    }


    fun callContactDialog(fragmentManager: FragmentManager) {
        val contactUsDialog = InfoDialog()
        contactUsDialog.show(fragmentManager, "contact us")
    }

    fun callCheckVersionDialog(fragmentManager: FragmentManager) {
        val checkVersionDialog = CheckVersionDialog()
        checkVersionDialog.show(fragmentManager, "check version")
    }

    fun callDetailEula(context: Context, urlString: String) {
        val intent =
            Intent(Intent.ACTION_VIEW).setData(Uri.parse(urlString))
        context.startActivity(intent)
    }

    fun setFloatingButtonItem(
        floatingButton: FloatingButton, activity: AppCompatActivity
    ): FloatingButton {
        floatingButton.clearAllItems()
        floatingButton.circleIcon.setImageDrawable(
            ContextCompat.getDrawable(
                activity,
                R.mipmap.btn_akg_logo
            )
        )
        if (CacheUtil.getPreferenceString(
                IConfig.LOGIN_TYPE,
                activity
            )?.equals(IConfig.LOGIN_GUEST)!!
        ) {
            floatingButton.addItem(
                FloatingItem(
                    ContextCompat.getDrawable(
                        activity,
                        R.mipmap.btn_bind_account
                    ), null, "Bind Account"
                )
            )

        } else {
            floatingButton.addItem(
                FloatingItem(
                    ContextCompat.getDrawable(
                        activity,
                        R.mipmap.btn_verify_account
                    ), null, "Account Info"
                )
            )
        }
        floatingButton.addItem(
            FloatingItem(
                ContextCompat.getDrawable(activity, R.mipmap.btn_fb),
                null,
                "FB Fanpage"
            )
        )
        floatingButton.addItem(
            FloatingItem(
                ContextCompat.getDrawable(activity, R.mipmap.btn_eula),
                null,
                "Eula"
            )
        )
        floatingButton.addItem(
            FloatingItem(
                ContextCompat.getDrawable(activity, R.mipmap.btn_contact_us),
                null, "Contact Us"
            )
        )
        floatingButton.addItem(
            FloatingItem(
                ContextCompat.getDrawable(activity, R.mipmap.btn_sdk_version),
                null, "SDK Version"
            )
        )
        floatingButton.addItem(
            FloatingItem(
                ContextCompat.getDrawable(activity, R.mipmap.btn_log_out),
                null, "Logout"
            )
        )
        floatingButton.addItem(
            FloatingItem(
                ContextCompat.getDrawable(activity, R.mipmap.x_btn),
                null,
                "Level Up"
            )
        )
        callGetAccount(activity)

        return floatingButton
    }

    fun callPaymentOttoPay(
        context: Context, userId: String, idProductGame: String,
        ottoPayCallback: OttoPaySDKCallback
    ) {
        val depositRequest = DepositRequest()
        depositRequest.user_id = userId
        depositRequest.game_product_id = idProductGame

        OrderPresenter(this)
            .onCreateDeposit(depositRequest, context)
        ottoPaySDKCallback = ottoPayCallback

    }

    private fun callGetAccount(activity: AppCompatActivity) {
        idUser = Hawk.get<String>(Constants.FIREBASE_ID)
        InfoPresenter(this).onGetCurrentUser(
            idUser.toString(),
            activity,
            activity
        )
    }

    fun callListGame(iView: IView, context: Context) {
        GamePresenter(iView)
            .onGameList(context)
    }


    fun callLoginDialog(
        activity: AppCompatActivity,
        loginSDKCallback: LoginSDKCallback
    ) {
        if (!CacheUtil.getPreferenceBoolean(IConfig.SESSION_LOGIN, activity)) {
            customCallback = loginSDKCallback
            val loginDialogFragment =
                LoginDialogFragment.newInstance(
                    activity.supportFragmentManager,
                    customCallback
                )
            val ftransaction = activity.supportFragmentManager.beginTransaction()
            ftransaction.addToBackStack("login")
            loginDialogFragment.show(ftransaction, "login")
        } else {
            Toast.makeText(activity, "You already logged in", Toast.LENGTH_LONG).show()
        }
    }

    override fun doOnSuccess(activity: AppCompatActivity, data: CurrentUserResponse) {
        if (CacheUtil.getPreferenceString(IConfig.LOGIN_TYPE, activity) == IConfig.LOGIN_PHONE) {
            data.data?.attributes?.phone_number
            CacheUtil.putPreferenceString(
                IConfig.SESSION_USERNAME,
                data.data?.attributes?.phone_number!!,
                activity
            )
        } else {
            if (data.data?.attributes?.email != null) {
                CacheUtil.putPreferenceString(
                    IConfig.SESSION_USERNAME, data.data?.attributes?.email!!,
                    activity
                )
            } else {
                CacheUtil.putPreferenceString(
                    IConfig.SESSION_USERNAME, "",
                    activity
                )
            }
        }
        if (data.data?.attributes?.uid != null) {
            CacheUtil.putPreferenceString(
                IConfig.SESSION_UID, data.data?.attributes?.uid!!,
                activity
            )
        }

        if (data.meta?.code == 401) {
            Hawk.deleteAll()
            CacheUtil.clearPreference(activity)
            val loginDialogFragment = LoginDialogFragment()
            val ftransaction = activity.supportFragmentManager.beginTransaction()
            ftransaction.addToBackStack("login")
            loginDialogFragment.show(ftransaction, "login")
        }


    }

    override fun doOnError(message: String?) {
    }

    override fun doOnSuccessCreateDeposit(data: DepositResponse) {
        ottoPaySDKCallback.onSuccessPayment(data)
    }

    override fun onSuccesEula(eulaReponse: EulaResponse) {
        eulaSdkCallBack.onSuccesEula(eulaReponse)
    }

    override fun handleError(message: String) {
        ottoPaySDKCallback.onFailedPayment(message)
        eulaSdkCallBack.onErrorEula(message)
    }

    override fun handleRetryConnection() {
    }
}