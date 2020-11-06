package com.akggame.akg_sdk.ui.dialog.forget

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.extension.animateScale
import com.akggame.akg_sdk.extension.beginDelayedTransition
import com.akggame.akg_sdk.dao.api.model.request.SendOtpRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.extension.doAfterAnimate
import com.akggame.akg_sdk.presenter.RegisterPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.register.OTPIView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_forgot.*
import kotlinx.android.synthetic.main.content_dialog_forgot.view.*

class ForgetDialog() : BaseDialogFragment(), OTPIView {

    companion object {
        fun newInstance(fm: FragmentManager?): ForgetDialog {
            return ForgetDialog(fm)
        }
    }

    constructor(fm: FragmentManager?):this(){
        myFragmentManager = fm
    }

    lateinit var mView: View
    var isGenerateOTP: Boolean = false
    var sendOtpRequest: SendOtpRequest = SendOtpRequest()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_forgot, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    override fun doOnSuccessGenerate(data: BaseResponse) {
        isGenerateOTP = true
        clOtp.animateScale(1.0f, 1.0f, 350L / 2)
            .doAfterAnimate {
                clOtp.beginDelayedTransition(350L)
                clOtp.visibility = View.VISIBLE
            }
    }

    override fun doOnSuccessCheck(data: BaseResponse) {
        var bundle = Bundle()
        if (myFragmentManager!=null){
            bundle.putString("phone","+62"+mView.etPhoneNumber.text.toString())
            val updatePassword = UpdatePasswordDialog.newInstance(myFragmentManager,bundle)
            val ftransaction = myFragmentManager!!.beginTransaction()
            ftransaction.addToBackStack("update password")
            updatePassword.show(ftransaction, "update password")
            customDismiss()
        }
    }

    fun initialize() {
        sendOtpRequest.auth_provider = "akg"
        sendOtpRequest.game_provider = CacheUtil.getPreferenceString(IConfig.SESSION_GAME,requireActivity())
        sendOtpRequest.otp_type = "forgot_password"

        mView.btnNext.setOnClickListener {
            if (mView.etPhoneNumber.text.isNotEmpty()) {
                val presenter = RegisterPresenter(this@ForgetDialog)
                sendOtpRequest.phone_number = "+62"+mView.etPhoneNumber.text.toString()
                if (!isGenerateOTP) {
                    presenter.sendOtp(sendOtpRequest, requireActivity() as Context)
                } else {
                    sendOtpRequest.otp_code = mView.etOtpCode.text.toString()
                    presenter.checkOtp(sendOtpRequest, requireActivity())
                }
            } else {
                Toast.makeText(requireActivity(), "field cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        mView.tvResendOTP.setOnClickListener {
            if (mView.etPhoneNumber.text.isNotEmpty()) {
                val presenter = RegisterPresenter(this@ForgetDialog)
                sendOtpRequest.phone_number = "+62" + mView.etPhoneNumber.text.toString()
                presenter.sendOtp(sendOtpRequest, requireActivity())
            } else {
                Toast.makeText(requireActivity(), "phone cannot be empty", Toast.LENGTH_LONG).show()
            }
        }
    }
}