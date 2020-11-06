package com.akggame.akg_sdk.ui.dialog.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_contact_us.view.*

class InfoDialog():BaseDialogFragment() {

    lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_contact_us, container, true)
        return mView
    }
    constructor(fm: FragmentManager?):this(){
        myFragmentManager = fm
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    fun initialize(){
        mView.ivClose.setOnClickListener {
            this.dismiss()
        }

        mView.btnNext.setOnClickListener {
            this.dismiss()
        }
    }
}