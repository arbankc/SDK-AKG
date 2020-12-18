package com.akggame.akg_sdk.presenter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustConfig
//import com.adjust.sdk.LogLevel
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.IConfig.*
import com.akggame.akg_sdk.IConfig.Companion.ADJUST_LOGIN
import com.akggame.akg_sdk.IConfig.Companion.ADJUST_LOGOUT
import com.akggame.akg_sdk.IConfig.Companion.ADJUST_PAYMENT_FAILED
import com.akggame.akg_sdk.IConfig.Companion.ADJUST_PAYMENT_SUCCESS
import com.akggame.akg_sdk.IConfig.Companion.ADJUST_REGISTER_FAILED
import com.akggame.akg_sdk.IConfig.Companion.ADJUST_REGISTER_SUCCESS
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.response.*
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.dialog.banner.BannerIView
import com.akggame.akg_sdk.ui.dialog.menu.AccountIView
import com.akggame.akg_sdk.ui.dialog.menu.CheckVersionIView
import com.akggame.akg_sdk.ui.dialog.register.OTPIView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.orhanobut.hawk.Hawk
import io.reactivex.disposables.Disposable

class InfoPresenter(val mIView: IView) {


    fun onGetCurrentUser(userId: String?, activity: AppCompatActivity, context: Context) {
        MainDao().onCheckCurrentUser(userId.toString(), context)
            .subscribe(object : RxObserver<CurrentUserResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    Log.d("TESTING API", "onNext")
                    t as CurrentUserResponse
                    if (t.meta?.code == 200) {
                        (mIView as AccountIView).doOnSuccess(activity, t)
                        Hawk.put(Constants.DATA_LOGIN, t)
                    } else {
                        (mIView as AccountIView).doOnError(t.data?.message)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                }
            })
    }

    fun onGetSDKVersion(context: Context) {
        MainDao().onGetSDKVersion(context)
            .subscribe(object : RxObserver<SDKVersionResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    t as SDKVersionResponse
                    if (t.meta?.code == 200) {
                        (mIView as CheckVersionIView).doOnSuccess(t)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : " + e.toString())

                }
            })
    }

    fun onGetBanner(context: Context) {
        MainDao().onGetBanner(context)
            .subscribe(object : RxObserver<BannerResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    t as BannerResponse
                    if (t.meta?.code == 200) {
                        (mIView as BannerIView).doOnSuccess(t)
                    }
//                    else{
//                        (mIView).handleError(t.)
//                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : " + e.toString())

                }
            })
    }

    fun onGetSDKConf(gameProvider: String, application: Application, context: Context) {
        MainDao().onGetSDKConfig(gameProvider)
            .subscribe(object : RxObserver<SDKConfigResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    t as SDKConfigResponse
                    if (t.meta?.code == 200) {
                        if (t.data?.adjust?.app_token != null) {
                            CacheUtil.putPreferenceString(
                                IConfig.ADJUST_APP_TOKEN,
                                t.data?.adjust?.app_token!!,
                                context
                            )

                            initAdjust(application, t.data?.adjust?.app_token!!)
                            if (t.data?.adjust?.events != null) {
                                t.data?.adjust?.events!!.forEach {
                                    when (it.name) {
                                        ADJUST_LOGIN -> CacheUtil.putPreferenceString(
                                            ADJUST_LOGIN,
                                            it.token,
                                            context
                                        )
                                        ADJUST_LOGOUT -> CacheUtil.putPreferenceString(
                                            ADJUST_LOGOUT,
                                            it.token,
                                            context
                                        )
                                        ADJUST_PAYMENT_FAILED -> CacheUtil.putPreferenceString(
                                            ADJUST_PAYMENT_FAILED,
                                            it.token,
                                            context
                                        )
                                        ADJUST_PAYMENT_SUCCESS -> CacheUtil.putPreferenceString(
                                            ADJUST_PAYMENT_SUCCESS,
                                            it.token,
                                            context
                                        )
                                        ADJUST_REGISTER_FAILED -> CacheUtil.putPreferenceString(
                                            ADJUST_REGISTER_FAILED,
                                            it.token,
                                            context
                                        )
                                        ADJUST_REGISTER_SUCCESS -> CacheUtil.putPreferenceString(
                                            ADJUST_REGISTER_SUCCESS,
                                            it.token,
                                            context
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : " + e.toString())

                }
            })

    }

    private fun initAdjust(application: Application, appToken: String) {
//        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
//        val config = AdjustConfig(application, appToken, environment)
//        config.setLogLevel(LogLevel.VERBOSE); // enable all logs
//        Adjust.onCreate(config)
//        Log.d("Adjust", "Initiate")
//        application.registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
    }

    private class AdjustLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityStarted(p0: Activity) {

        }

        override fun onActivityDestroyed(p0: Activity) {

        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

        }

        override fun onActivityStopped(p0: Activity) {

        }

        override fun onActivityCreated(p0: Activity, p1: Bundle?) {

        }

        override fun onActivityResumed(activity: Activity) {
//            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
//            Adjust.onPause()
        }
    }

}