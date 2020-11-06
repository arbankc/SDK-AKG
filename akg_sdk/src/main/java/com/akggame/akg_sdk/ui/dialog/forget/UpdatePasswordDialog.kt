package com.akggame.akg_sdk.ui.dialog.forget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.request.UpdatePasswordRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.presenter.RegisterPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.SuccessDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_input_password.*
import kotlinx.android.synthetic.main.content_dialog_input_password.view.*

class UpdatePasswordDialog():BaseDialogFragment(),UpdatePasswordIView {

    companion object {
        fun newInstance(mFragmentManager: FragmentManager?, bundle: Bundle): UpdatePasswordDialog {
            val mDialog = UpdatePasswordDialog(mFragmentManager)
            mDialog.arguments = bundle
            return mDialog
        }
    }

    constructor(fm: FragmentManager?):this(){
        myFragmentManager = fm
    }

    var phone: String? = ""
    lateinit var mView: View
    val presenter = RegisterPresenter(this@UpdatePasswordDialog)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_input_password, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        val bundle = this.arguments
        if (bundle != null) {
            phone = bundle.getString("phone")
        }
        tvTitle.text = resources.getString(R.string.forgot_title)
        tvUID.text = resources.getString(R.string.forgot_desc)
        initialize()
    }

    override fun doOnSuccess(data: BaseResponse) {
        val bundle = Bundle()
        if(myFragmentManager!=null){
            bundle.putString("phone", phone)
            val successDialog = SuccessDialogFragment.newInstance(myFragmentManager, bundle)
            val ftransaction = myFragmentManager!!.beginTransaction()
            ftransaction.addToBackStack("success")
            successDialog.show(ftransaction, "success")
            customDismiss()
        }
    }

    fun initialize() {
        val model = UpdatePasswordRequest()
        mView.btnNext.setOnClickListener {
            if (mView.etPassword.text.isNotEmpty() && mView.etConfPassword.text.isNotEmpty()) {
                if (etPassword.text.toString().length > 7 && etConfPassword.text.toString().length > 7) {
                    if (etPassword.text.toString().equals(etConfPassword.text.toString())) {
                        model.auth_provider = "akg"
                        model.game_provider = CacheUtil.getPreferenceString(IConfig.SESSION_GAME,requireActivity())
                        model.password = etPassword.text.toString()
                        model.phone_number = phone
                        presenter.onUpdatePassword(model,requireActivity())
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