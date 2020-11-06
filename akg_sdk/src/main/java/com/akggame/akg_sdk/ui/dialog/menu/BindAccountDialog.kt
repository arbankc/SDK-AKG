package com.akggame.akg_sdk.ui.dialog.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.MenuSDKCallback
import com.akggame.akg_sdk.dao.api.model.request.BindSocMedRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.presenter.BindAccountPresenter
import com.akggame.akg_sdk.ui.component.FloatingButton
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.menu.binding.VerifyAccountDialog
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.DeviceUtil
import com.akggame.android.sdk.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.content_dialog_bind_account.btnBack
import kotlinx.android.synthetic.main.content_dialog_bind_account.fbLoginButton
import kotlinx.android.synthetic.main.content_dialog_bind_account.view.*
import kotlinx.android.synthetic.main.content_dialog_bind_account.view.fbLoginButton

class BindAccountDialog() : BaseDialogFragment(), BindAccountIView {


    lateinit var mView: View
    lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mFloatingButton: FloatingButton
    lateinit var mMenuSDKCallback: MenuSDKCallback
    val presenter = BindAccountPresenter(this)


    companion object {
        fun newInstance(
            fm: FragmentManager?,
            floatingButton: FloatingButton,
            menuSDKCallback: MenuSDKCallback
        ): BindAccountDialog {

            return BindAccountDialog(fm, floatingButton, menuSDKCallback)
        }
    }

    constructor(
        fm: FragmentManager?,
        floatingButton: FloatingButton,
        menuSDKCallback: MenuSDKCallback
    ) : this() {
        myFragmentManager = fm
        mFloatingButton = floatingButton
        mMenuSDKCallback = menuSDKCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.content_dialog_bind_account, container, true)
        return mView
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    override fun doOnSuccess(data: BaseResponse, socmedType: String) {
        CacheUtil.putPreferenceString(IConfig.SESSION_TOKEN,data.BaseDataResponse?.token!!,requireActivity())
        when(socmedType){
            "google" -> {
                CacheUtil.putPreferenceString(IConfig.LOGIN_TYPE,IConfig.LOGIN_GOOGLE,requireActivity())
                mMenuSDKCallback.onSuccessBind(data.BaseDataResponse!!.token!!,AKG_SDK.LOGIN_GOOGLE)

            }
            "facebook" -> {
                CacheUtil.putPreferenceString(IConfig.LOGIN_TYPE,IConfig.LOGIN_FACEBOOK,requireActivity())
                mMenuSDKCallback.onSuccessBind(data.BaseDataResponse!!.token!!,AKG_SDK.LOGIN_FACEBOOK)
            }
        }
        AKG_SDK.resetFloatingButton(requireActivity() as AppCompatActivity)
        customDismiss()
        clearBackStack()
    }

    fun initialize() {
        setFacebookLogin()
        setGoogleLogin()
        mView.ivClose.setOnClickListener {
            this.dismiss()
        }
        btnBack.setOnClickListener {
            if(myFragmentManager!=null){
                val verifyDialog = VerifyAccountDialog.newInstance(myFragmentManager)
                val ftransaction = myFragmentManager!!.beginTransaction()
                ftransaction.addToBackStack("verify account")
                verifyDialog.show(ftransaction, "verify account")
                customDismiss()
            }
        }
    }


    override fun doOnError(message: String, socmedType: String) {

        when (socmedType) {
            "google" -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(IConfig.GOOGLE_CLIENT_ID)
                    .build()

                val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

                mGoogleSignInClient.signOut().addOnCompleteListener {
                }
            }
            "facebook" -> {
                LoginManager.getInstance().logOut()
            }
        }

        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    fun setFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        mView.fbLoginButton.setFragment(this)
        mView.fbLoginButton.setPermissions(arrayListOf("email"))

        mView.btnBindFacebook.setOnClickListener {
            if (DeviceUtil.getImei(requireActivity()).isNotEmpty()) {
                mView.fbLoginButton.performClick()
            }
        }

        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val data = BindSocMedRequest(
                    result?.accessToken?.token,
                    "facebook",
                    DeviceUtil.getImei(requireActivity()),
                    "Android",
                    DeviceUtil.getDeviceName()
                )
                presenter.onBindAccount(data, "facebook", requireActivity())
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }
        })
    }

    /*
  * GOOGLE SIGN IN----------------------------------------->
  **/
    fun setGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(IConfig.GOOGLE_CLIENT_ID)
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        mView.btnBindGoogle.setOnClickListener {
            if (DeviceUtil.getImei(requireActivity()).isNotEmpty()) {
                val signInIntent = mGoogleSignInClient.getSignInIntent()
                startActivityForResult(signInIntent, 101)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val data = BindSocMedRequest(
                account?.idToken,
                "google",
                DeviceUtil.getImei(requireActivity()),
                "Android",
                DeviceUtil.getDeviceName()
            )
            presenter.onBindAccount(data, "google", requireActivity())
        } catch (e: ApiException) {
            Log.w("FRAGMENT_GOOGLE", "signInResult:failed code=" + e.statusCode)
        }
    }
}