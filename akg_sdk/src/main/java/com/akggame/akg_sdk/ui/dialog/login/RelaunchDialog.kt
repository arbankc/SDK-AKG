package com.akggame.akg_sdk.ui.dialog.login

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.RelaunchSDKCallback
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.menu.LogoutIView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.android.sdk.R
import com.github.ajalt.timberkt.d
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.content_dialog_relaunch.*
import kotlinx.android.synthetic.main.content_dialog_relaunch.view.*


class RelaunchDialog : BaseDialogFragment(), LogoutIView {
    lateinit var mView: View
    lateinit var countDown: CountDownTimer
    var timeMilis: Long = 10000
    var firebaseUser: FirebaseUser? = null

    companion object {
        lateinit var callback: RelaunchSDKCallback
        fun newInstance(callback: RelaunchSDKCallback): RelaunchDialog {
            val mDialogFragment = RelaunchDialog()
            this.callback = callback

            return mDialogFragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.content_dialog_relaunch, container, true)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        firebaseUser = FirebaseAuth.getInstance().currentUser
        tvPhoneNumber.text =
            "Welcome " + CacheUtil.getPreferenceString(IConfig.SESSION_USERNAME, requireActivity())
        tvUID.text =
            "UID : " + CacheUtil.getPreferenceString(IConfig.SESSION_UID, requireActivity())


        mView.btnContinue.setOnClickListener {
            callback.onContinue()
            countDown.cancel()
//            dismiss()
        }

        startCountDown()

        mView.btnRelogin.setOnClickListener {
            deleteLogin()
            countDown.cancel()
            callback.onReLogin()
        }

        firebaseUser?.getIdToken(true)?.addOnSuccessListener {
            val idToken = it.token
            CacheUtil.putPreferenceString(
                IConfig.SESSION_TOKEN,
                idToken.toString(),
                activity as Context
            )
            Hawk.put(IConfig.SESSION_TOKEN, idToken)
        }

    }

    fun startCountDown() {
        countDown = object : CountDownTimer(timeMilis, 1000) {
            override fun onFinish() {
                mView.btnContinue.text = "Continue in 0s"
                mView.btnContinue.performClick()
                countDown.cancel()
                activity?.finish()
            }

            override fun onTick(millisUntilFinished: Long) {
                timeMilis = millisUntilFinished
                mView.btnContinue.text =
                    "Continue in " + (timeMilis / 1000).toString() + "s"
            }
        }

        countDown.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeMilis.let { outState.putLong("timeRunning", it) }
        d { "respon Time milis $timeMilis" }
    }

}