package com.akggame.akg_sdk.ui.dialog.login

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.RelaunchSDKCallback
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.presenter.LogoutPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.menu.LogoutIView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_relaunch.*
import kotlinx.android.synthetic.main.content_dialog_relaunch.view.*


class RelaunchDialog() : BaseDialogFragment(), LogoutIView {


    lateinit var mView: View
    private val presenter = LogoutPresenter(this)
    lateinit var countDown : CountDownTimer

    companion object {
        lateinit var callback: RelaunchSDKCallback
        fun newInstance(callback: RelaunchSDKCallback): RelaunchDialog {
            val mDialogFragment = RelaunchDialog()
           this.callback = callback

            return mDialogFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_relaunch, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    override fun doSuccess() {
       this.dismiss()

        callback.onReLogin()
    }

    override fun doError() {
        Toast.makeText(requireActivity(), "Error logout", Toast.LENGTH_LONG).show()
    }

    fun initialize() {
        tvPhoneNumber.text = "Welcome " + CacheUtil.getPreferenceString(IConfig.SESSION_USERNAME, requireActivity())
        tvUID.text = "UID : "+CacheUtil.getPreferenceString(IConfig.SESSION_UID, requireActivity())
        countDown = object : CountDownTimer(4000, 1000) {
            override fun onFinish() {
                mView.btnContinue.text = "Continue in 0s"
                mView.btnContinue.performClick()
            }

            override fun onTick(millisUntilFinished: Long) {
                mView.btnContinue.text = "Continue in " + (millisUntilFinished / 1000).toString() + "s"
            }
        }
        countDown.start()

        mView.btnContinue.setOnClickListener {
            dismiss()
            countDown.cancel()
            callback.onContinue()
        }

        mView.btnRelogin.setOnClickListener {
            val loginType = CacheUtil.getPreferenceString(IConfig.LOGIN_TYPE, requireActivity())
            when (loginType) {
                IConfig.LOGIN_PHONE -> SocmedDao.logoutPhone(requireActivity(), this, presenter)
                IConfig.LOGIN_GUEST -> SocmedDao.logoutGuest(requireActivity(), this, presenter)
                IConfig.LOGIN_GOOGLE -> SocmedDao.logoutGoogle(requireActivity(), this, presenter)
                IConfig.LOGIN_FACEBOOK -> SocmedDao.logoutFacebook(requireActivity(), this, presenter)
            }
            countDown.cancel()
        }
    }
}