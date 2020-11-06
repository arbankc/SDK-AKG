package com.akggame.akg_sdk.ui.dialog.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.response.CurrentUserResponse
import com.akggame.akg_sdk.presenter.InfoPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_account.*
import kotlinx.android.synthetic.main.content_dialog_account.view.*

class AccountDialog() : BaseDialogFragment(), AccountIView {

    lateinit var mView: View
    val presenter: InfoPresenter = InfoPresenter(this)

    companion object {
        fun newInstance(fm: FragmentManager?): AccountDialog {
            return AccountDialog(fm)
        }
    }

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_account, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        initialize()
        presenter.onGetCurrentUser(requireActivity() as AppCompatActivity,requireActivity())
    }

    override fun doOnSuccess(activity:AppCompatActivity,data: CurrentUserResponse) {
        if (CacheUtil.getPreferenceString(IConfig.LOGIN_TYPE, activity) == IConfig.LOGIN_PHONE) {
            if(etOldPassword!=null){
                etOldPassword.text = data.data?.attributes?.phone_number

            }
        } else {
            if(etOldPassword!=null){
                etOldPassword.text = data.data?.attributes?.email
            }
        }
        if(etNewPassword!=null){
            etNewPassword.text = data.data?.attributes?.uid
        }
    }

    override fun doOnError(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun initialize() {
        mView.ivClose.setOnClickListener {
            customDismiss()
        }
        if(!CacheUtil.getPreferenceString(IConfig.LOGIN_TYPE,requireActivity())!!.equals(IConfig.LOGIN_PHONE)){
            mView.tvChangePassword.visibility = View.INVISIBLE
        }

        mView.tvChangePassword.setOnClickListener {
            if(myFragmentManager!=null){
                val changePasswordDialog = ChangePasswordDialog.newInstance(myFragmentManager)
                changePasswordDialog.show(myFragmentManager!!.beginTransaction(), "change password")
                customDismiss()
            }

        }

        btnBack.setOnClickListener {
            customDismiss()
        }
    }
}