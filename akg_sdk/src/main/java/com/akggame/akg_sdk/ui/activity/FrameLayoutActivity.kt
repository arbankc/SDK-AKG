package com.akggame.akg_sdk.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.akggame.akg_sdk.baseextend.BaseActivity
import com.akggame.akg_sdk.ui.dialog.login.LoginDialogFragment
import com.akggame.akg_sdk.ui.fragment.GameListProductFragment
import com.akggame.akg_sdk.ui.fragment.paymentmethod.PaymentOttoFragment
import com.akggame.akg_sdk.util.Constants
import com.akggame.android.sdk.R

class FrameLayoutActivity : BaseActivity() {
    var fragment: Fragment? = null
    var typePayment: String? = null
    var getPos: Int? = null
    var idGameProduct: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame_layout)

        initial()
    }

    private fun initial() {
        getPos = intent.getIntExtra("pos", 0)
        typePayment = intent.getStringExtra(Constants.TYPE_PAYMENT)
        idGameProduct = intent.getStringExtra(Constants.DATA_GAME_PRODUCT)

        initPositionFragment(getPos)

    }

    private fun initPositionFragment(getPos: Int?) {
        when (getPos) {
            1 -> {
                setPositonFragment(1)
            }
            2 -> {
                setPositonFragment(2)
            }
            3 -> {
                setPositonFragment(3)
            }
            4 -> {
                setPositonFragment(4)
            }
            5 -> {
                setPositonFragment(5)
            }
            6 -> {
                setPositonFragment(6)
            }
            7 -> {
                setPositonFragment(7)
            }
        }
    }

    private fun setPositonFragment(position: Int) {
        when (position) {
            1 -> {
                gotoFragment(GameListProductFragment.newInstance(typePayment.toString(), ""))
            }
            3 -> {
                gotoFragment(PaymentOttoFragment.newInstance(idGameProduct.toString(), ""))
            }
            4 -> {
                gotoFragment(LoginDialogFragment())
            }
            4 -> {
                gotoFragment(LoginDialogFragment())
            }
        }
        loadFragment(fragment)
    }

    private fun gotoFragment(fragment: Fragment) {
        return loadFragment(fragment)
    }


    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()
        }
    }
}