package com.akggame.akg_sdk.ui.dialog.login

//import com.akggame.akg_sdk.ui.BaseActivity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.IConfig.Companion.SESSION_TOKEN
import com.akggame.akg_sdk.callback.LoginSDKCallback
import com.akggame.akg_sdk.callback.StartGameSDKCallback
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList
import com.akggame.akg_sdk.presenter.LoginPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.GameListDialogFragment
import com.akggame.akg_sdk.ui.dialog.PhoneLoginDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.newandroid.sdk.R
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
import kotlinx.android.synthetic.main.content_dialog_login.view.*
import org.json.JSONException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginDialogFragment() : BaseDialogFragment(), FacebookCallback<LoginResult> {

    lateinit var mView: View
    lateinit var callbackManager: CallbackManager
    lateinit var startGameSDKCallback: StartGameSDKCallback
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val presenter = LoginPresenter(this@LoginDialogFragment)
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

//    var loginButtonFb: LoginButton? = null

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    companion object {
        var mLoginCallback: LoginSDKCallback? = null
        fun newInstance(
            mFragmentManager: FragmentManager,
            loginCallback: LoginSDKCallback?
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
        AppEventsLogger.activateApp(requireActivity().application)
        initialize()
        return mView
    }

    override fun onStart() {
        super.onStart()
        mDismissed = false
        mShownByMe = true
        onViewDestroyed = false
        currentUser = auth?.currentUser
    }


    /*
     * Facebook SIGN IN----------------------------------------->
     */

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { _, response ->
            try {
                emailFb = response.jsonObject.getString("email")
                nameFb = response.jsonObject.getString("first_name")
                tokenFb = loginResult.accessToken.toString()
                idFb = response.jsonObject.getString("id")
                var profileURL = ""
                if (Profile.getCurrentProfile() != null) {
                    profileURL = ImageRequest.getProfilePictureUri(
                        Profile.getCurrentProfile().id,
                        400,
                        400
                    ).toString()
                }
                statusLogin = "facebook"
                firebaseAuthWithFb(loginResult.accessToken)
            } catch (e: JSONException) {
                val gameListDialogFragment =
                    currentUser?.let {
                        GameListDialogFragment.newInstance(
                            currentUser!!.uid,
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

        mView.btnBindGoogle.setOnClickListener {
            showDialogLoading(false)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 20)
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
        setLoginFb()
        printKeyHash()

        mView.btnBack.setOnClickListener {
            this.customDismiss()
            changeToPhoneLogin()
        }

        mView.btnGuest.setOnClickListener { //
            showDialogLoading(true)
            loginGuestFirebase()

        }

        mView.btnBindFacebook?.setOnClickListener {
            showDialogLoading(true)
            callLoginFb()
        }
    }

    private fun setLoginFb() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, this)
    }


    private fun callLoginFb() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile", "email"))
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

    private fun printKeyHash() {
        // Add code to print out the key hash
        try {
            val info = activity?.packageManager?.getPackageInfo(
                activity!!.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info?.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("respon Key has:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    private fun loginGuestFirebase() {
        activity?.let {
            auth?.signInAnonymously()
                ?.addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        currentUser = auth?.currentUser
                        statusLogin = "guest"
                        showDialogLoading(false)
                        getTokenClientAuth(currentUser, statusLogin.toString())
                    } else {
                        // If sign in fails, display a message to the user.
                        showToast("Gagal guest")
                        showDialogLoading(false)
                        val gameListDialogFragment =
                            currentUser?.let {
                                GameListDialogFragment.newInstance(
                                    currentUser!!.uid,
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
            showDialogLoading(true)
            firebaseAuthWithGoogle(account?.idToken.toString())
        } catch (e: ApiException) {
            Log.w("FRAGMENT_GOOGLE", "signInResult:failed code=" + e.statusCode)
        }
    }

    fun getTokenClientAuth(user: FirebaseUser?, typeLogin: String) {
        showDialogLoading(true)
        user?.getIdToken(true)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val idToken: String = it.result?.token.toString()
                    Log.d("Get token ", "+ $idToken")
                    Hawk.put(Constants.FIREBASE_ID, currentUser?.uid.toString())
                    CacheUtil.putPreferenceString(SESSION_TOKEN, idToken, activity as Context)
                    Hawk.put(SESSION_TOKEN, idToken)
                    when {
                        typeLogin.equals("google", ignoreCase = true) -> {
                            val gameListDialogFragment =
                                currentUser?.let {
                                    GameListDialogFragment.newInstance(
                                        currentUser!!.uid,
                                        statusLogin, emailFb,
                                        mLoginCallback!!,
                                        nameFb,
                                        "sukses",
                                        it
                                    )
                                }
                            gameListDialogFragment?.show(fragmentManager!!, "")
                            showDialogLoading(false)
                        }
                        typeLogin.equals("facebook", ignoreCase = true) -> {
                            val gameListDialogFragment =
                                currentUser?.let {
                                    GameListDialogFragment.newInstance(
                                        currentUser!!.uid,
                                        statusLogin, emailFb,
                                        mLoginCallback!!,
                                        nameFb,
                                        "sukses",
                                        it
                                    )
                                }
                            gameListDialogFragment?.show(fragmentManager!!, "")
                            showDialogLoading(false)
                        }
                        typeLogin.equals("guest", ignoreCase = true) -> {
                            val gameListDialogFragment =
                                currentUser?.let {
                                    GameListDialogFragment.newInstance(
                                        currentUser!!.uid,
                                        statusLogin, emailFb,
                                        mLoginCallback!!,
                                        nameFb,
                                        "sukses",
                                        it
                                    )
                                }

                            gameListDialogFragment?.show(fragmentManager!!, "")
                            showDialogLoading(false)
                        }
                    }
                } else {
                    // Handle error -> task.getException();
                    showToast("Terjadi kesalahan pada server")
                    showDialogLoading(false)
                }
            }

    }

    fun firebaseAuthWithFb(token: AccessToken) {
        showDialogLoading(true)
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("respon ", "signInWithCredential:success")
                    currentUser = auth?.currentUser
                    getTokenClientAuth(currentUser, statusLogin.toString())
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("respon ", "signInWithCredential:failure", it.exception)
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
                                currentUser!!.uid,
                                statusLogin, emailFb,
                                mLoginCallback!!,
                                nameFb,
                                "failed",
                                it
                            )
                        }
                    showDialogLoading(false)
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

    override fun onCancel() {
        showDialogLoading(false)
        println("respon Cancel ")
    }

    override fun onSuccess(result: LoginResult?) {
        showDialogLoading(false)
        result?.let {
            setFacebookData(it)
        }
    }

    override fun onError(error: FacebookException?) {
        error?.printStackTrace()
        showDialogLoading(false)
        println("respon Error fb ${error?.message}")
        showToast(error?.message)
        if (error is FacebookAuthorizationException) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut()
            }
        }
    }

}