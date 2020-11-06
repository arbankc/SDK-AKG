package com.akggame.akg_sdk.ui.dialog.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.dao.api.model.request.ChangePasswordRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.presenter.UpdatePresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_change_password.*
import kotlinx.android.synthetic.main.content_dialog_change_password.view.*

class ChangePasswordDialog() : BaseDialogFragment(),ChangeIView {

    lateinit var mView: View
    val presenter = UpdatePresenter(this)

    companion object {
        fun newInstance(fm: FragmentManager?): ChangePasswordDialog {
            return ChangePasswordDialog(fm)
        }
    }

    constructor(fm: FragmentManager?):this(){
        myFragmentManager = fm
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_change_password, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    override fun doOnSuccess(data: BaseResponse) {
        Toast.makeText(requireActivity(),data.BaseDataResponse?.message,Toast.LENGTH_LONG).show()
        this.dismiss()
    }

    override fun doOnError(message: String?) {
        Toast.makeText(requireActivity(),message,Toast.LENGTH_LONG).show()
    }

    fun initialize() {
        mView.ivClose.setOnClickListener {
            this.dismiss()
        }

        btnBack.setOnClickListener {
            var model  = ChangePasswordRequest()
            model.old_password = etOldPassword.text.toString()
            model.password = etNewPassword.text.toString()
            model.password_confirmation = etVerifyPassword.text.toString()
            presenter.onChangePassword(model,requireActivity())
        }
    }
}