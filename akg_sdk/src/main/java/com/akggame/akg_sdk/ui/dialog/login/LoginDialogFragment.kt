package com.akggame.akg_sdk.ui.dialog.login

//import com.akggame.akg_sdk.ui.BaseActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.LoginSDKCallback
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.dao.api.model.request.GuestLoginRequest
import com.akggame.akg_sdk.presenter.LoginPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.PhoneLoginDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.DeviceUtil
import com.akggame.android.sdk.R
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.ajalt.timberkt.d
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.content_dialog_login.*
import kotlinx.android.synthetic.main.content_dialog_login.view.*
import org.json.JSONException


class LoginDialogFragment() : BaseDialogFragment(), LoginIView {

    lateinit var mView: View
    lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val presenter = LoginPresenter(this@LoginDialogFragment)
    var mDismissed: Boolean = true
    var mShownByMe = false
    var onViewDestroyed = true
    var auth: FirebaseAuth? = null

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }


    companion object {
        private lateinit var mLoginCallback: LoginSDKCallback
        fun newInstance(
            mFragmentManager: FragmentManager,
            loginCallback: LoginSDKCallback
        ): LoginDialogFragment {
            val mDialogFragment = LoginDialogFragment(mFragmentManager)
            mLoginCallback = loginCallback
            return mDialogFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView =
            LayoutInflater.from(requireActivity() as Context)
                .inflate(R.layout.content_dialog_login, null, false)

        return mView
    }

    override fun onStart() {
        super.onStart()
        mDismissed = false
        mShownByMe = true
        onViewDestroyed = false

        AppEventsLogger.activateApp(requireActivity().application)

        initialize()

        val currentUser = auth?.currentUser

//        if (currentUser != null) {
////            showToast("onContinue")
//        } else showToast("Belun login")


    }

    override fun doOnSuccess(isFirstLogin: Boolean, token: String, loginType: String) {
//        val id = DeviceUtil.decoded(token).toObject<UserData>()
        mLoginCallback.onResponseSuccess(token, "", loginType)
        CacheUtil.putPreferenceString(IConfig.SESSION_PIW, "", requireActivity())
        SocmedDao.setAdjustEventLogin(isFirstLogin, requireActivity())
        dismiss()
    }

    override fun doOnError(message: String) {
        mLoginCallback.onResponseFailed(message)
        customDismiss()
        clearBackStack()
    }

    /*
     * Facebook SIGN IN----------------------------------------->
     */
    fun setFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        mView.fbLoginButton.fragment = this
        mView.fbLoginButton.setPermissions(arrayListOf("email"))
        mView.btnBindFacebook.setOnClickListener {
            if (DeviceUtil.getImei(requireActivity()).isNotEmpty()) {
                mView.fbLoginButton.performClick()
            }
        }

        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.let { setFacebookData(it) }
                d { "respon Data fb ${result?.accessToken}" }

            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                if (error is FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                    }
                    showToast("errrorr ${error.message}")
                }
                d { "respon Data fb erro " }

            }
        })
    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { _, response ->
            try {
                d { " responData Fb $response" }
                val email = response.jsonObject.getString("email")
                val firstName = response.jsonObject.getString("first_name")
                val lastName = response.jsonObject.getString("last_name")
                var profileURL = ""
                if (Profile.getCurrentProfile() != null) {
                    profileURL = ImageRequest.getProfilePictureUri(
                        Profile.getCurrentProfile().id,
                        400,
                        400
                    ).toString()
                }

                val model = FacebookAuthRequest()
                model.email = email
                model.name = firstName
                model.phone_number = "-"
                model.access_token = loginResult.accessToken.toString()
                model.auth_provider = "akg"
                model.device_id = DeviceUtil.getImei(requireActivity())
                model.game_provider =
                    CacheUtil.getPreferenceString(IConfig.SESSION_GAME, requireActivity())
                model.operating_system = "Android"
                model.phone_model = DeviceUtil.getDeviceName()

                presenter.facebookLogin(model, requireActivity())
            } catch (e: JSONException) {
                d { "respon Error ${e.message}" }
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()
    }


    /*
     * GOOGLE SIGN IN----------------------------------------->
     **/
    fun setGoogleLogin() {
        mGoogleSignInClient = SocmedDao.setGoogleSigninClient(requireContext())

        btnBindGoogle.setOnClickListener {
            if (DeviceUtil.getImei(requireActivity()).isNotEmpty()) {
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, 20)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 20) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }


    private fun initialize() {
        auth = FirebaseAuth.getInstance()
        setGoogleLogin()
        setFacebookLogin()
        mView.btnBack.setOnClickListener {
            if (DeviceUtil.getImei(requireActivity()).isNotEmpty()) {
                this.customDismiss()
                changeToPhoneLogin()
            }
        }

        mView.btnGuest.setOnClickListener {
            if (DeviceUtil.getImei(requireActivity()).isNotEmpty()) {
                val model = GuestLoginRequest(
                    "guest",
                    DeviceUtil.getImei(requireActivity()),
                    CacheUtil.getPreferenceString(IConfig.SESSION_GAME, requireActivity()),
                    "Android",
                    DeviceUtil.getDeviceName()
                )
                presenter.guestLogin(model, requireActivity())
            }
        }
    }

    private fun changeToPhoneLogin() {
        if (myFragmentManager != null) {
            val phoneLoginDialogFragment =
                PhoneLoginDialogFragment.newInstance(myFragmentManager, mLoginCallback)
            val ftransaction = myFragmentManager!!.beginTransaction()
            ftransaction.addToBackStack("phone")
            phoneLoginDialogFragment.show(ftransaction, "phone")
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            d { "respon Login account ${account?.idToken}" }
            firebaseAuthWithGoogle(account?.idToken.toString(), account?.idToken.toString())
        } catch (e: ApiException) {
            Log.w("FRAGMENT_GOOGLE", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, token: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth?.currentUser
                    val model = FacebookAuthRequest()
                    model.email = user?.email
                    model.access_token = token
                    model.name = user?.displayName
                    model.phone_number = "-"
                    model.auth_provider = "akg"
                    model.device_id = DeviceUtil.getImei(requireActivity())
                    model.game_provider =
                        CacheUtil.getPreferenceString(IConfig.SESSION_GAME, requireActivity())
                    model.operating_system = "Android"
                    model.phone_model = DeviceUtil.getDeviceName()
                    d { "respon Login succes ${user?.email}" }
                    LoginPresenter(this@LoginDialogFragment).googleLogin(model, requireActivity())
                } else {
                    d { "respon Login failed " }

                }
            }

    }

}