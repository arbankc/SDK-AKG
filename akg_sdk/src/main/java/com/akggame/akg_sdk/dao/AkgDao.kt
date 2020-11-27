package com.akggame.akg_sdk.dao

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.akggame.akg_sdk.*
import com.akggame.akg_sdk.dao.api.model.FloatingItem
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.dao.api.model.response.CurrentUserResponse
import com.akggame.akg_sdk.presenter.InfoPresenter
import com.akggame.akg_sdk.presenter.ProductPresenter
import com.akggame.akg_sdk.ui.adapter.FloatingAdapterListener
import com.akggame.akg_sdk.ui.component.FloatingButton
import com.akggame.akg_sdk.ui.dialog.banner.BannerDialog
import com.akggame.akg_sdk.ui.dialog.login.LoginDialogFragment
import com.akggame.akg_sdk.ui.dialog.login.RelaunchDialog
import com.akggame.akg_sdk.ui.dialog.menu.*
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.android.sdk.R
import com.android.billingclient.api.SkuDetails
import com.orhanobut.hawk.Hawk


class AkgDao : AccountIView {

    private lateinit var customCallback: LoginSDKCallback
    private val productPresenter = ProductPresenter(this)
    private val presenter = InfoPresenter(this)

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
            application, context, callback
        )
    }

    fun callBrowserFanPage(context: Context) {
        val url = "https://www.facebook.com/akggames/"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
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

    fun setFloatingButtonListener(
        activity: AppCompatActivity,
        floatingButton: FloatingButton,
        context: Context,
        menuSDKCallback: MenuSDKCallback
    ) {
        val onItemClickListener: FloatingAdapterListener = object : FloatingAdapterListener {
            override fun onItemClick(position: Int, floatingItem: FloatingItem) {
                val contactUsDialog = InfoDialog()
                val checkVersionDialog = CheckVersionDialog()
                val bindAccountDialog = BindAccountDialog.newInstance(
                    activity.supportFragmentManager,
                    floatingButton,
                    menuSDKCallback
                )
                val logoutDialog = LogoutDialog.newInstance(menuSDKCallback)
                val accountDialog = AccountDialog.newInstance(activity.supportFragmentManager)
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
                    1 -> callBrowserFanPage(context)

                    2 -> Toast.makeText(context, "eula", Toast.LENGTH_LONG).show()

                    3 -> contactUsDialog.show(activity.supportFragmentManager, "contact us")

                    4 -> checkVersionDialog.show(activity.supportFragmentManager, "check version")

                    5 -> logoutDialog.show(activity.supportFragmentManager, "logout")

                }
            }
        }

        floatingButton.floatingAdapterListener = onItemClickListener
    }

    fun setFloatingButtonItem(
        floatingButton: FloatingButton, activity: AppCompatActivity
    ): FloatingButton {


        floatingButton.circleIcon.setImageDrawable(
            ContextCompat.getDrawable(
                activity,
                R.mipmap.btn_akg_logo
            )
        )
        floatingButton.clearAllItems()
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
        callGetAccount(activity)

        return floatingButton
    }

    private fun callGetAccount(activity: AppCompatActivity) {
        val idUser = Hawk.get<String>(Constants.DATA_USER_ID)
        InfoPresenter(this).onGetCurrentUser(
            idUser,
            activity,
            activity
        )
    }

    fun callLoginDialog(
        activity: AppCompatActivity,
        gameName: String,
        loginSDKCallback: LoginSDKCallback
    ) {
        CacheUtil.putPreferenceString(IConfig.SESSION_GAME, gameName, activity)
        if (!CacheUtil.getPreferenceBoolean(IConfig.SESSION_LOGIN, activity)) {
            customCallback = loginSDKCallback
            val loginDialogFragment =
                LoginDialogFragment.newInstance(
                    activity.supportFragmentManager,
                    customCallback
                )
            val ftransaction = activity.supportFragmentManager.beginTransaction()
            loginDialogFragment.clearBackStack()
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
    }

    override fun doOnError(message: String?) {
    }

    override fun handleError(message: String) {
    }

    override fun handleRetryConnection() {
    }
}