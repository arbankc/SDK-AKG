package com.akggame.akg_sdk.ui.activity.eula

import android.os.Bundle
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.dao.api.model.response.EulaResponse
import com.akggame.akg_sdk.presenter.EulaPresenter
import com.akggame.newandroid.sdk.R
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_eula.*

class EulaActivity : BaseActivity(), EulaIView {

    var eulaPresenter: EulaPresenter? = null
    var gameId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eula)

        inital()
    }

    private fun inital() {
        gameId = Hawk.get<String>("gameId")
        eulaPresenter = EulaPresenter(this)
        eulaPresenter?.onGetEula(this, gameId.toString())
    }

    override fun onSuccesEula(eulaReponse: EulaResponse) {
        showToast("Sukses dapat eula")
        tvDetailEula.text = eulaReponse.data?.attributes?.contents
    }

    override fun handleError(message: String) {

    }

    override fun handleRetryConnection() {
    }
}