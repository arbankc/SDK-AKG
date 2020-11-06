package com.akggame.akg_sdk.ui.dialog.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.extension.animateScale
import com.akggame.akg_sdk.extension.beginDelayedTransition
import com.akggame.akg_sdk.dao.api.model.request.SendOtpRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.extension.doAfterAnimate
import com.akggame.akg_sdk.presenter.RegisterPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_forgot.*
import kotlinx.android.synthetic.main.content_dialog_registration.*
import kotlinx.android.synthetic.main.content_dialog_registration.clOtp
import kotlinx.android.synthetic.main.content_dialog_registration.etOtpCode
import kotlinx.android.synthetic.main.content_dialog_registration.view.*

class OTPDialog() : BaseDialogFragment(), OTPIView {
    lateinit var mView: View
    var isGenerateOTP: Boolean = false
    var sendOtpRequest: SendOtpRequest = SendOtpRequest()

    companion object {
        fun newInstance(fm: FragmentManager?): OTPDialog {
            return OTPDialog(fm)
        }
    }

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_registration, container, true)
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
        bundle.putString("phone", "+62" + mView.etPhoneNumber.text.toString())
        if(myFragmentManager!=null){
            val setPasswordDialog = SetPasswordDialog.newInstance(myFragmentManager, bundle)
            val ftransaction = myFragmentManager!!.beginTransaction()
            ftransaction.addToBackStack("set password")
            setPasswordDialog.show(ftransaction, "set password")
            customDismiss()
        }

    }

    fun initialize() {
        mView.etOtpCode.imeOptions = EditorInfo.IME_ACTION_DONE
        sendOtpRequest.auth_provider = "akg"
        sendOtpRequest.game_provider = CacheUtil.getPreferenceString(IConfig.SESSION_GAME,requireActivity())
        sendOtpRequest.otp_type = "registration"

        mView.btnNext.setOnClickListener {
            if (mView.etPhoneNumber.text.isNotEmpty()) {
                val presenter = RegisterPresenter(this@OTPDialog)
                sendOtpRequest.phone_number = "+62" + mView.etPhoneNumber.text.toString()
                if (!isGenerateOTP) {
                    presenter.sendOtp(sendOtpRequest, requireActivity())
                } else {
                    sendOtpRequest.otp_code = mView.etOtpCode.text.toString()
                    presenter.checkOtp(sendOtpRequest, requireActivity())
                }
            } else {
                Toast.makeText(requireActivity(), "phone cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        mView.tvResendOTP.setOnClickListener {
            if (mView.etPhoneNumber.text.isNotEmpty()) {
                val presenter = RegisterPresenter(this@OTPDialog)
                sendOtpRequest.phone_number = "+62" + mView.etPhoneNumber.text.toString()
                presenter.sendOtp(sendOtpRequest, requireActivity())
            } else {
                Toast.makeText(requireActivity(), "phone cannot be empty", Toast.LENGTH_LONG).show()
            }
        }
        mView.etOtpCode.requestFocus()
    }
}