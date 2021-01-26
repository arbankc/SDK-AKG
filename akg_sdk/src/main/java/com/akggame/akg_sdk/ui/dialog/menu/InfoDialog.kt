package com.akggame.akg_sdk.ui.dialog.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.android.sdk.R
import kotlinx.android.synthetic.main.content_dialog_contact_us.*
import kotlinx.android.synthetic.main.content_dialog_contact_us.view.*

class InfoDialog : BaseDialogFragment() {

    lateinit var mView: View
    var imageButtonClose: ImageButton? = null
    var btnBackDialog: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.content_dialog_contact_us, container, true)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageButtonClose = view.findViewById(R.id.ivClose)
        btnBackDialog = view.findViewById(R.id.btnBack)

        initialize()
    }

    fun initialize() {
        imageButtonClose?.setOnClickListener {
            dismiss()
        }

        btnBackDialog?.setOnClickListener {
            dismiss()
        }

        btnBackDialog?.setText("Kembali")
    }
}