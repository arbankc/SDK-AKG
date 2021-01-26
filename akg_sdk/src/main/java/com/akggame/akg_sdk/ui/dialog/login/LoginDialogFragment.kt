package com.akggame.akg_sdk.ui.dialog.login

//import com.akggame.akg_sdk.ui.BaseActivity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.IConfig.Companion.SESSION_TOKEN
import com.akggame.akg_sdk.LoginSDKCallback
import com.akggame.akg_sdk.StartGameSDKCallback
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.dao.api.model.response.CurrentUserResponse
import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList
import com.akggame.akg_sdk.presenter.GamePresenter
import com.akggame.akg_sdk.presenter.LoginPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.GameListDialogFragment
import com.akggame.akg_sdk.ui.dialog.PhoneLoginDialogFragment
import com.akggame.akg_sdk.ui.dialog.menu.AccountIView
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
import com.google.firebase.auth.*
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.content_dialog_login.*
import kotlinx.android.synthetic.main.content_dialog_login.view.*
import org.json.JSONException


class LoginDialogFragment() : BaseDialogFragment() {

    lateinit var mView: View
    lateinit var callbackManager: CallbackManager
    lateinit var startGameSDKCallback: StartGameSDKCallback
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val presenter = LoginPresenter(this@LoginDialogFragment)
    val presenterGame = GamePresenter(this@LoginDialogFragment)
    var mDismissed: Boolean = true
    var mShownByMe = false
    var onViewDestroyed = true
    var auth: FirebaseAuth? = null
    var currentUser: FirebaseUser? = null
    var idToken: String? = null
    var statusLogin: String? = null

    var dialogListGame: Dialog? = null
    var dataItemGameList: List<DataItemGameList>? = null

    var emailFb: String? = null
    var nameFb: String? = null
    var tokenFb: String? = null
    var idFb: String? = null


    val accountIView: AccountIView = (object : AccountIView {
        override fun doOnSuccess(activity: AppCompatActivity, data: CurrentUserResponse) {
            val bundle = Bundle()
            bundle.putString("UID", data.data?.id)
            bundle.putString("Email", data.data?.attributes?.email)
            bundle.putString("Phone Number", data.data?.attributes?.phone_number)
            bundle.putString("Auth Provider ", data.data?.attributes?.auth_provider)
//            bundle.putString("Game Id", data.data?.attributes?.game_provider)
            bundle.putString("Last Login", data.data?.attributes?.last_login)
            bundle.putString("Register Time", data.data?.attributes?.register_time)
            bundle.putString("Game Provider number", data.data?.attributes?.game_provider)
            hitEventFirebase("Info Login User", bundle)
        }

        override fun doOnError(message: String?) {
            TODO("Not yet implemented")
        }

        override fun handleError(message: String) {
            TODO("Not yet implemented")
        }

        override fun handleRetryConnection() {
            TODO("Not yet implemented")
        }


    })

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }


    companion object {
        var mLoginCallback: LoginSDKCallback? = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mDismissed = false
        mShownByMe = true
        onViewDestroyed = false

        AppEventsLogger.activateApp(requireActivity().application)

        initialize()

        currentUser = auth?.currentUser
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
                }

            }
        })
    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { _, response ->
            try {
                d { " responData Fb $response" }
                emailFb = response.jsonObject.getString("email")
                nameFb = response.jsonObject.getString("first_name")
                tokenFb = loginResult.accessToken.toString()
                idFb = response.jsonObject.getString("id")

                val lastName = response.jsonObject.getString("last_name")
                var profileURL = ""
                if (Profile.getCurrentProfile() != null) {
                    profileURL = ImageRequest.getProfilePictureUri(
                        Profile.getCurrentProfile().id,
                        400,
                        400
                    ).toString()
                }

                statusLogin = "facebook"
                getTokenClientAuth(currentUser, statusLogin.toString())


            } catch (e: JSONException) {
                d { "respon Error ${e.message}" }
                val gameListDialogFragment =
                    currentUser?.let {
                        GameListDialogFragment.newInstance(
                            statusLogin, emailFb,
                            mLoginCallback!!,
                            nameFb,
                            "failed",
                            it
                        )
                    }
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
        dialogListGame = dialogCustom()

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
//
                loginGuestFirebase()
            }

        }
    }

    private fun loginUpdateGuestFirebase() {
        val credential = EmailAuthProvider.getCredential("elhananayo@gmail.com", "123456")
        activity?.let {
            auth?.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                    } else {
                        d { "gagal update guest" }
                    }
                }
        }

    }

    private fun loginGuestFirebase() {
        activity?.let {
            auth?.signInAnonymously()
                ?.addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth?.currentUser
                        statusLogin = "guest"
                        getTokenClientAuth(user, statusLogin.toString())

                    } else {
                        // If sign in fails, display a message to the user.
                        showToast("Gagal geuest")
                        val gameListDialogFragment =
                            currentUser?.let {
                                GameListDialogFragment.newInstance(
                                    statusLogin, emailFb,
                                    mLoginCallback!!,
                                    nameFb,
                                    "failed",
                                    it
                                )
                            }
                    }

                }
        }
    }

    private fun changeToPhoneLogin() {
        if (myFragmentManager != null) {
            val phoneLoginDialogFragment =
                PhoneLoginDialogFragment.newInstance(myFragmentManager, mLoginCallback!!)
            val ftransaction = myFragmentManager!!.beginTransaction()
            ftransaction.addToBackStack("phone")
            phoneLoginDialogFragment.show(ftransaction, "phone")
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            idToken = account?.idToken
            d { "respon Login account ${account?.idToken}" }
            firebaseAuthWithGoogle(account?.idToken.toString())
        } catch (e: ApiException) {
            Log.w("FRAGMENT_GOOGLE", "signInResult:failed code=" + e.statusCode)
        }
    }

    fun getTokenClientAuth(user: FirebaseUser?, typeLogin: String) {
        user!!.getIdToken(true)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val idToken: String = it.result?.token.toString()
                    Log.d("Get token ", "+ $idToken")
                    CacheUtil.putPreferenceString(SESSION_TOKEN, idToken, activity as Context)
                    Hawk.put(SESSION_TOKEN, idToken)

                    when {
                        typeLogin.equals("google", ignoreCase = true) -> {
                            val gameListDialogFragment =
                                currentUser?.let {
                                    GameListDialogFragment.newInstance(
                                        statusLogin, emailFb,
                                        mLoginCallback!!,
                                        nameFb,
                                        "sukses",
                                        it
                                    )
                                }
                            gameListDialogFragment?.show(fragmentManager!!, "")
                        }
                        typeLogin.equals("facebook", ignoreCase = true) -> {
                            val gameListDialogFragment =
                                currentUser?.let {
                                    GameListDialogFragment.newInstance(
                                        statusLogin, emailFb,
                                        mLoginCallback!!,
                                        nameFb,
                                        "sukses",
                                        it
                                    )
                                }
                            gameListDialogFragment?.show(fragmentManager!!, "")
                        }
                        typeLogin.equals("guest", ignoreCase = true) -> {
                            val gameListDialogFragment =
                                currentUser?.let {
                                    GameListDialogFragment.newInstance(
                                        statusLogin, emailFb,
                                        mLoginCallback!!,
                                        nameFb,
                                        "sukses",
                                        it
                                    )
                                }

                            gameListDialogFragment?.show(fragmentManager!!, "")
                        }
                    }
                } else {
                    // Handle error -> task.getException();
                    showToast("gagal token ")
                }
            }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser = auth?.currentUser
                    statusLogin = "google"
                    getTokenClientAuth(currentUser, statusLogin.toString())

                } else {
                    val gameListDialogFragment =
                        currentUser?.let {
                            GameListDialogFragment.newInstance(
                                statusLogin, emailFb,
                                mLoginCallback!!,
                                nameFb,
                                "failed",
                                it
                            )
                        }
                }
            }

    }


    override fun onResume() {
        super.onResume()
        if (!CacheUtil.getPreferenceBoolean(IConfig.SESSION_LOGIN, requireActivity())) {
            signOut()
        }
    }

    private fun signOut() {
        auth?.signOut()
        activity?.let {
            mGoogleSignInClient.signOut().addOnCompleteListener(it) {

            }
        }
    }


}