package com.akggame.akg_sdk.ui.dialog.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.newandroid.sdk.R
import kotlinx.android.synthetic.main.content_dialog_version.view.*
import com.akggame.akg_sdk.dao.api.model.response.SDKVersionResponse
import com.akggame.akg_sdk.presenter.InfoPresenter
import com.akggame.newandroid.sdk.BuildConfig


class CheckVersionDialog : BaseDialogFragment(), CheckVersionIView {

    lateinit var mView: View
    var presenter = InfoPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.content_dialog_version, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        presenter.onGetSDKVersion(requireActivity())
        initialize()
    }

    fun initialize() {
        mView.ivClose.setOnClickListener {
            this.dismiss()
        }
        mView.btnNext.setOnClickListener {
            this.dismiss()
        }

        mView.btnNext.text = "Kembali"
    }

    override fun doOnSuccess(data: SDKVersionResponse) {
        if (mView.tvVersion != null) {
            if (data.data?.attributes?.version_number == BuildConfig.VERSION_CODE) {
                mView.tvVersion.text =
                    resources.getString(R.string.check_update_desc, BuildConfig.VERSION_NAME)
            } else {
                mView.tvVersion.text =
                    resources.getString(R.string.update_sdk_version, BuildConfig.VERSION_NAME)
            }
        }
    }
}