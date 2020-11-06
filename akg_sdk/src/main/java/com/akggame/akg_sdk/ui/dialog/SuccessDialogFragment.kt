package com.akggame.akg_sdk.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.ui.dialog.login.LoginDialogFragment
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_success.view.*

class SuccessDialogFragment() : BaseDialogFragment() {

    lateinit var mView: View
    var phone: String? = ""


    companion object {
        fun newInstance(mFragmentManager: FragmentManager?, bundle: Bundle): SuccessDialogFragment {
            val mDialog = SuccessDialogFragment(mFragmentManager)
            mDialog.arguments = bundle
            return mDialog
        }
    }

    constructor(fm: FragmentManager?):this(){
        myFragmentManager = fm
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_success, container, true)

        return mView
    }

    override fun onStart() {
        super.onStart()
        val bundle = this.arguments
        if (bundle != null) {
            phone = bundle.getString("phone")
        }
        initialize()
    }

    override fun setOnBackPressed(){
        dialog?.setOnKeyListener(object : View.OnKeyListener, DialogInterface.OnKeyListener {
            override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
                return false
            }

            override fun onKey(p0: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    return true
                } else {
                    return false
                }
            }
        })
    }

    fun initialize() {
        mView.btnNext.setOnClickListener {
            restartBackStack()
        }
        mView.tvUID.setText("Your account with number " + phone + " already been made")
    }

}