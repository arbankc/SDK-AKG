package com.akggame.akg_sdk.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.LoginSDKCallback
import com.akggame.akg_sdk.dao.api.model.request.PhoneAuthRequest
import com.akggame.akg_sdk.dao.pojo.UserData
import com.akggame.akg_sdk.presenter.LoginPresenter
import com.akggame.akg_sdk.ui.dialog.forget.ForgetDialog
import com.akggame.akg_sdk.ui.dialog.login.LoginDialogFragment
import com.akggame.akg_sdk.ui.dialog.login.LoginIView
import com.akggame.akg_sdk.ui.dialog.register.OTPDialog
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.DeviceUtil
import com.akggame.android.sdk.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_dialog_login_phone.*
import kotlinx.android.synthetic.main.content_dialog_login_phone.view.*

class PhoneLoginDialogFragment() : BaseDialogFragment(), LoginIView {

    companion object {
        private lateinit var mLoginCallback: LoginSDKCallback

        fun newInstance(fm: FragmentManager?, loginCallback: LoginSDKCallback): PhoneLoginDialogFragment {
            mLoginCallback = loginCallback

            return PhoneLoginDialogFragment(fm)
        }
    }

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    private lateinit var mView: View
    val presenter = LoginPresenter(this@PhoneLoginDialogFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_login_phone, container, true)
        return mView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val style = DialogFragment.STYLE_NO_FRAME
        setStyle(style, theme)
    }

    override fun onStart() {
        super.onStart()
        initView()
    }



    override fun doOnError(message: String) {
        mLoginCallback.onResponseFailed(message)
    }

    fun initView() {

        mView.tvResendOTP.setOnClickListener {
            if(myFragmentManager!=null){
                val otpDialog = OTPDialog.newInstance(myFragmentManager)
                val ftransaction = myFragmentManager!!.beginTransaction()
                ftransaction.addToBackStack("registration")
                otpDialog.show(ftransaction, "registration")
                customDismiss()
            }
        }

        mView.btnBack.setOnClickListener {
            val phoneAuthRequest = PhoneAuthRequest()
            phoneAuthRequest.phone_number = "+62" + etPhoneNumber.text.toString()
            phoneAuthRequest.password = etOtpCode.text.toString()
            phoneAuthRequest.auth_provider = "akg"
            phoneAuthRequest.game_provider = CacheUtil.getPreferenceString(IConfig.SESSION_GAME,requireActivity())
            phoneAuthRequest.device_id = DeviceUtil.getImei(requireActivity())
            phoneAuthRequest.phone_model = DeviceUtil.getDeviceName()
            phoneAuthRequest.operating_system = "Android"
            presenter.phoneLogin(phoneAuthRequest, requireActivity())
        }

        mView.tvForgotPassword.setOnClickListener {
            if(myFragmentManager!=null){
                val forgetDialog = ForgetDialog.newInstance(myFragmentManager)
                val ftransaction = myFragmentManager!!.beginTransaction()
                ftransaction.addToBackStack("forget")
                forgetDialog.show(ftransaction, "forget")
                customDismiss()
            }
        }
    }

    override fun doOnSuccess(isLoginType:Boolean,token: String, loginType: String) {
        val id = DeviceUtil.decoded(token).toObject<UserData>()
        mLoginCallback.onResponseSuccess(token,id.id, loginType)
    }
}