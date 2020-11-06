package com.akggame.akg_sdk.ui.dialog.menu.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.request.PhoneBindingRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.presenter.BindAccountPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.register.SetPasswordIView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.DeviceUtil
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_verification.*
import kotlinx.android.synthetic.main.content_dialog_verification.view.*
import kotlinx.android.synthetic.main.content_dialog_verify.view.*
import kotlinx.android.synthetic.main.content_dialog_verify.view.btnNext
import kotlinx.android.synthetic.main.content_dialog_verify.view.ivClose

class VerificationDialog() :BaseDialogFragment(),SetPasswordIView {

    lateinit var mView: View
    var phone: String? = ""
    val presenter = BindAccountPresenter(this)

    companion object {
        fun newInstance(mFragmentManager: FragmentManager?, bundle: Bundle): VerificationDialog {
            val mDialog = VerificationDialog(mFragmentManager)
            mDialog.arguments = bundle
            return mDialog
        }
    }

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_verification, container, true)
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
        CacheUtil.putPreferenceString(IConfig.LOGIN_TYPE,IConfig.LOGIN_PHONE,requireActivity())
        AKG_SDK.getMenuCallback().onSuccessBind(data.BaseDataResponse!!.token!!,AKG_SDK.LOGIN_PHONE)
        AKG_SDK.resetFloatingButton(requireActivity() as AppCompatActivity)
        customDismiss()
        clearBackStack()
        Toast.makeText(requireActivity(),data.BaseDataResponse!!.message,Toast.LENGTH_LONG).show()

    }

    override fun doOnError() {
    }

    fun initialize() {
        mView.ivClose.setOnClickListener {
            customDismiss()
            clearBackStack()
        }

        mView.btnNext.setOnClickListener {
            if (mView.etPassword.text.isNotEmpty() && mView.etConfPassword.text.isNotEmpty()) {
                if (etPassword.text.toString().length > 7 && etConfPassword.text.toString().length > 7) {
                    if (etPassword.text.toString().equals(etConfPassword.text.toString())) {
                        val model = PhoneBindingRequest(
                            DeviceUtil.getImei(requireActivity()),
                            "android",
                            etPassword.text.toString(),
                            DeviceUtil.getDeviceName(),
                            phone!!
                        )

                        presenter.onBindPhoneNumber(model, requireActivity())
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