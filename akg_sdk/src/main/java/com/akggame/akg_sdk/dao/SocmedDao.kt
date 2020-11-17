package com.akggame.akg_sdk.dao

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustEvent
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.presenter.LogoutPresenter
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.DeviceUtil
import com.akggame.android.sdk.R
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics

object SocmedDao {

    fun logoutFacebook(activity: Activity, iView: IView, presenter: LogoutPresenter) {

        LoginManager.getInstance().logOut()
        presenter.logout(activity)
    }

    fun logoutGoogle(activity: Activity, iView: IView, presenter: LogoutPresenter) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(IConfig.GOOGLE_CLIENT_ID)
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

        mGoogleSignInClient.signOut().addOnCompleteListener {
            presenter.logout(activity)
        }
    }

    fun logoutPhone(activity: Activity, iView: IView, presenter: LogoutPresenter) {
        presenter.logout(activity)
    }

    fun logoutGuest(activity: Activity, iView: IView, presenter: LogoutPresenter) {
        presenter.logout(activity)
    }

    fun setSuccessFacebookRequest(result: LoginResult?, context: Context): FacebookAuthRequest {
        val model = FacebookAuthRequest()
//        model.access_token = result?.accessToken?.token
        model.auth_provider = "facebook"
        model.device_id = DeviceUtil.getImei(context)
        model.game_provider =
            CacheUtil.getPreferenceString(IConfig.SESSION_GAME, context)
        model.operating_system = "android"
        model.phone_model = DeviceUtil.getDeviceName()

        return model
    }

    fun setAdjustEventLogin(isFirstLogin: Boolean, context: Context) {
//        if (CacheUtil.getPreferenceString(IConfig.ADJUST_LOGIN, context) != null) {
//            val adjustEvent =
//                AdjustEvent(CacheUtil.getPreferenceString(IConfig.ADJUST_LOGIN, context))
//            adjustEvent.addCallbackParameter(
//                "user_id",
//                CacheUtil.getPreferenceString(IConfig.SESSION_PIW, context)
//            )
//            if (isFirstLogin) {
//                adjustEvent.addCallbackParameter("cost_type", "CPI")
//                adjustEvent.addCallbackParameter("cost_amount", "1.0")
//                adjustEvent.addCallbackParameter("device_id", DeviceUtil.getImei(context))
//                adjustEvent.addCallbackParameter("game_name", CacheUtil.getPreferenceString(IConfig.SESSION_GAME, context))
//            }
//            Log.d("PIW ", CacheUtil.getPreferenceString(IConfig.SESSION_PIW, context))
//            Adjust.trackEvent(adjustEvent)
//        }
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        bundle.putString(
            "user_id",
            CacheUtil.getPreferenceString(IConfig.SESSION_PIW, context)
        )
        bundle.putString(
            "game_name",
            CacheUtil.getPreferenceString(IConfig.SESSION_GAME, context)
        )
        bundle.putString(
            "device_id",
            DeviceUtil.getImei(context)
        )
        bundle.putBoolean("is_first_login",isFirstLogin)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

    }

    fun setGoogleSigninClient(context: Context): GoogleSignInClient {
        val google_client_id: String = context.getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(google_client_id)
            .build()

        return GoogleSignIn.getClient(context, gso)
    }
}