package com.akggame.akg_sdk.ui.dialog.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustEvent
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.MenuSDKCallback
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.presenter.LogoutPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.android.sdk.R
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.content_dialog_logout.*
import kotlinx.android.synthetic.main.content_dialog_logout.view.*

class LogoutDialog() : BaseDialogFragment(), LogoutIView {

    lateinit var mView: View
    val presenter = LogoutPresenter(this)

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    companion object {
        internal lateinit var menuSDKCallback: MenuSDKCallback

        fun newInstance(callback: MenuSDKCallback): LogoutDialog {
            val mDialogFragment = LogoutDialog()
            menuSDKCallback = callback
            return mDialogFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.content_dialog_logout, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    override fun doSuccess() {
        this.dismiss()
        setAdjustEventLogout()
        menuSDKCallback.onLogout()
    }

    override fun doError() {
        Toast.makeText(requireActivity(), "Error logout", Toast.LENGTH_LONG).show()
    }

    fun initialize() {
        mView.ivClose.setOnClickListener {
            this.dismiss()
        }
        btnNext.setOnClickListener {
            if (CacheUtil.getPreferenceBoolean(IConfig.SESSION_LOGIN, requireActivity())) {
                val loginType = CacheUtil.getPreferenceString(IConfig.LOGIN_TYPE, requireActivity())
                when (loginType) {
                    IConfig.LOGIN_PHONE -> SocmedDao.logoutPhone(requireActivity(), this, presenter)
                    IConfig.LOGIN_GUEST -> SocmedDao.logoutGuest(requireActivity(), this, presenter)
                    IConfig.LOGIN_GOOGLE -> SocmedDao.logoutGoogle(
                        requireActivity(),
                        this,
                        presenter
                    )
                    IConfig.LOGIN_FACEBOOK -> SocmedDao.logoutFacebook(
                        requireActivity(),
                        this,
                        presenter
                    )
                }
            } else {
                Toast.makeText(requireActivity(), "You are not logged in", Toast.LENGTH_LONG).show()
            }
        }

        btnBack.setOnClickListener {
            this.dismiss()
        }
    }

    fun setAdjustEventLogout() {
//        if (CacheUtil.getPreferenceString(IConfig.ADJUST_LOGOUT, requireActivity()) != null) {
//            Adjust.trackEvent(
//                AdjustEvent(
//                    CacheUtil.getPreferenceString(
//                        IConfig.ADJUST_LOGOUT,
//                        requireActivity()
//                    )
//                )
//            )
//        }
        val bundle = Bundle()
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        bundle.putString(
            "user_id",
            CacheUtil.getPreferenceString(IConfig.SESSION_PIW, requireActivity())
        )
        firebaseAnalytics.logEvent("logout", bundle)
    }

}