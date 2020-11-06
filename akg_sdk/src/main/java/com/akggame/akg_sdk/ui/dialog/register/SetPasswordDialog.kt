package com.akggame.akg_sdk.ui.dialog.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustEvent
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.request.SignUpRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.presenter.RegisterPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.SuccessDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.DeviceUtil
import com.akggame.android.sdk.R
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.content_dialog_input_password.*
import kotlinx.android.synthetic.main.content_dialog_input_password.view.btnNext
import kotlinx.android.synthetic.main.content_dialog_input_password.view.etConfPassword
import kotlinx.android.synthetic.main.content_dialog_input_password.view.etPassword


class SetPasswordDialog() : BaseDialogFragment(), SetPasswordIView {

    lateinit var mView: View
    val presenter = RegisterPresenter(this@SetPasswordDialog)
    var phone: String? = ""

    companion object {
        fun newInstance(mFragmentManager: FragmentManager?, bundle: Bundle): SetPasswordDialog {
            val mDialog = SetPasswordDialog(mFragmentManager)
            mDialog.arguments = bundle
            return mDialog
        }
    }

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_input_password, container, true)
        return mView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            phone = bundle.getString("phone")
        }
    }

    override fun onStart() {
        super.onStart()
        initialize()

    }

    override fun doOnSuccess(data: BaseResponse) {
        val bundle = Bundle()
        bundle.putString("phone", phone)
        setAdjustEventRegisterSuccess()
        if (myFragmentManager!=null){
            val successDialog = SuccessDialogFragment.newInstance(myFragmentManager, bundle)
            val ftransaction = myFragmentManager!!.beginTransaction()
            ftransaction?.addToBackStack("success")
            successDialog.show(ftransaction, "success")
            customDismiss()
        }
    }
    override fun doOnError() {
        setAdjustEventRegisterError()
    }

    fun setAdjustEventRegisterSuccess() {
//        if(CacheUtil.getPreferenceString(IConfig.ADJUST_REGISTER_SUCCESS,requireActivity())!=null){
//            var adjustEvent = AdjustEvent(CacheUtil.getPreferenceString(IConfig.ADJUST_REGISTER_SUCCESS,requireActivity()))
//            Adjust.trackEvent(adjustEvent)
//        }
        val bundle = Bundle()
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        bundle.putString(
            "status",
            "success"
        )
        firebaseAnalytics.logEvent("register", bundle)
    }

    fun setAdjustEventRegisterError(){
//        if(CacheUtil.getPreferenceString(IConfig.ADJUST_REGISTER_FAILED,requireActivity())!=null){
//            Adjust.trackEvent(AdjustEvent(CacheUtil.getPreferenceString(IConfig.ADJUST_REGISTER_FAILED,requireActivity())))
//        }
        val bundle = Bundle()
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        bundle.putString(
            "status",
            "failed"
        )
        firebaseAnalytics.logEvent("register", bundle)
    }

    fun initialize() {
        val model = SignUpRequest()
        mView.btnNext.setOnClickListener {
            if (mView.etPassword.text.isNotEmpty() && mView.etConfPassword.text.isNotEmpty()) {
                if (etPassword.text.toString().length > 7 && etConfPassword.text.toString().length > 7) {
                    if (etPassword.text.toString().equals(etConfPassword.text.toString())) {
                        model.phone_model = DeviceUtil.getDeviceName()
                        model.auth_provider = "akg"
                        model.game_provider = CacheUtil.getPreferenceString(IConfig.SESSION_GAME,requireActivity())
                        model.device_id = DeviceUtil.getImei(requireActivity())
                        model.operating_system = "android"
                        model.password = etPassword.text.toString()
                        model.phone_number = phone
                        presenter.onSignUp(model, requireActivity())
                    } else {
                        Toast.makeText(requireActivity(), "Please check your password", Toast.LENGTH_LONG).show()
                    }
                }else{
                    if(!(etPassword.text.toString().length > 7)){
                        etPassword.error = "Password must be at least 8 characters"
                    }
                    if(!(etConfPassword.text.toString().length > 7)){
                        etConfPassword.error = "Password must be at least 8 characters"
                    }
                }

            } else {
                Toast.makeText(requireActivity(), "Fields cannot be empty", Toast.LENGTH_LONG).show()
            }
        }
    }
}