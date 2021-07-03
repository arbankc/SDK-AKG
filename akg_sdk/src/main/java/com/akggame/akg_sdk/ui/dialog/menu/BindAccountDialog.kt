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
import com.akggame.akg_sdk.callback.MenuSDKCallback
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.presenter.BindAccountPresenter
import com.akggame.akg_sdk.presenter.GamePresenter
import com.akggame.akg_sdk.ui.component.FloatingButton
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.menu.binding.VerifyAccountDialog
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.newandroid.sdk.R
import com.facebook.*
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.ajalt.timberkt.d
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.content_dialog_bind_account.*
import kotlinx.android.synthetic.main.content_dialog_bind_account.view.*
import org.json.JSONException

class BindAccountDialog() : BaseDialogFragment(), BindAccountIView {


    lateinit var mView: View
    lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mFloatingButton: FloatingButton
    lateinit var mMenuSDKCallback: MenuSDKCallback
    val presenter = BindAccountPresenter(this)
    val presenterGame = GamePresenter(this)
    var auth: FirebaseAuth? = null
    var currentUser: FirebaseUser? = null

    var emailAuth: String? = null
    var nameAuth: String? = null
    var tokenFb: String? = null
    var idFb: String? = null

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.content_dialog_bind_account, container, true)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        currentUser = auth?.currentUser
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    override fun doOnSuccess(data: BaseResponse, socmedType: String) {
        showDialogLoading(false)
        when (socmedType) {
            "google" -> {
                CacheUtil.putPreferenceString(
                    IConfig.LOGIN_TYPE,
                    IConfig.LOGIN_GOOGLE,
                    requireActivity()
                )
            }
            "facebook" -> {
                CacheUtil.putPreferenceString(
                    IConfig.LOGIN_TYPE,
                    IConfig.LOGIN_FACEBOOK,
                    requireActivity()
                )
            }
        }
        AKG_SDK.resetFloatingButton(requireActivity() as AppCompatActivity)
        customDismiss()
        clearBackStack()
    }

    fun initialize() {
        setGoogleLogin()
        setLoginFb()
        mView.ivClose.setOnClickListener {
            this.dismiss()
        }
        btnBack.setOnClickListener {
            if (myFragmentManager != null) {
                val verifyDialog = VerifyAccountDialog.newInstance(myFragmentManager)
                val ftransaction = myFragmentManager!!.beginTransaction()
                ftransaction.addToBackStack("verify account")
                verifyDialog.show(ftransaction, "verify account")
                customDismiss()
            }
        }
    }

    private fun setLoginFb() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.let { setFacebookData(it) }
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                    error?.printStackTrace()
                    println("respon Error fb ${error?.message}")
                    if (error is FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut()
                        }
                    }
                }

            })
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

    /*
  * GOOGLE SIGN IN----------------------------------------->
  **/
    fun setGoogleLogin() {
        mGoogleSignInClient = SocmedDao.setGoogleSigninClient(requireContext())
        mView.btnBindGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 101)
        }
    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { _, response ->
            try {
                d { " responData Fb $response" }
                emailAuth = response.jsonObject.getString("email")
                nameAuth = response.jsonObject.getString("first_name")
                tokenFb = loginResult.accessToken.toString()

                val lastName = response.jsonObject.getString("last_name")
                var profileURL = ""
                if (Profile.getCurrentProfile() != null) {
                    profileURL = ImageRequest.getProfilePictureUri(
                        Profile.getCurrentProfile().id,
                        400,
                        400
                    ).toString()
                }
                getTokenClientAuth(currentUser, "facebook")

            } catch (e: JSONException) {
                d { "respon Error ${e.message}" }
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()
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

            nameAuth = account?.displayName
            emailAuth = account?.email
            firebaseAuthWithGoogle(account?.idToken.toString(), auth!!)

        } catch (e: ApiException) {
            Log.w("FRAGMENT_GOOGLE", "signInResult:failed code=" + e.statusCode)
        }
    }


    fun hitEvent(model: FacebookAuthRequest, typeLogin: String?) {
        val packageName = Hawk.get<String>(Constants.ID_GAME_PROVIDER)
        val bundle = Bundle()
        bundle.putString("uid", model.firebase_id)
        bundle.putString("game_provider", packageName)
        bundle.putString("type_login", typeLogin)
        hitEventFirebase("Bind Account ", bundle)
    }

    private fun typeLogin(typeLogin: String?) {
        when {
            typeLogin.equals("facebook", ignoreCase = true) -> {
                CacheUtil.putPreferenceString(
                    IConfig.LOGIN_TYPE,
                    IConfig.LOGIN_FACEBOOK,
                    activity!!
                )
            }

            typeLogin.equals("google", ignoreCase = true) -> {
                CacheUtil.putPreferenceString(
                    IConfig.LOGIN_TYPE,
                    IConfig.LOGIN_GOOGLE,
                    activity!!
                )
            }
            typeLogin.equals("guest", ignoreCase = true) -> {
                CacheUtil.putPreferenceString(
                    IConfig.LOGIN_TYPE,
                    IConfig.LOGIN_GUEST,
                    activity!!
                )
            }
        }


    }

    private fun firebaseAuthWithGoogle(idToken: String, auth: FirebaseAuth) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser = auth.currentUser
                    getTokenClientAuth(currentUser, "google")
                } else {
                    d { "respon Login failed " }
                }
            }

    }

    fun getTokenClientAuth(user: FirebaseUser?, typeLogin: String?) {
        user!!.getIdToken(true)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val idToken: String = it.result?.token.toString()
                    // Send token to your backend via HTTPS
                    // ...
                    Log.d("Get token ", "+ $idToken")
                    Hawk.put(IConfig.SESSION_TOKEN, idToken)

                    if (typeLogin.equals("facebook", ignoreCase = true))
                        hitLoginBindAccount(emailAuth, nameAuth, "facebook")
                    else if (typeLogin.equals("google", ignoreCase = true))
                        hitLoginBindAccount(emailAuth, nameAuth, "google")
                } else {
                    // Handle error -> task.getException();
                    showToast("gagal token ")
                }
            }

    }

    private fun hitLoginBindAccount(email: String?, name: String?, typeLogin: String?) {
        showDialogLoading(true)
        val getChaceLogin = Hawk.get<FacebookAuthRequest>(Constants.DATA_UPSERT)
        val firebaseId = Hawk.get<String>(Constants.FIREBASE_ID)

        val model = FacebookAuthRequest()
        model.firebase_id = currentUser?.uid
        model.email = email
        model.access_token = getChaceLogin.access_token
        model.name = name
        model.phone_number = "-"
        model.auth_provider = "akg"

        model.device_id = getChaceLogin.device_id
        model.game_provider = getChaceLogin.game_provider
        model.operating_system = "Android"
        model.phone_model = getChaceLogin.phone_model
        model.login_type = typeLogin

        typeLogin(typeLogin)
        hitEvent(model, typeLogin)

        Hawk.put(Constants.DATA_UPSERT, model)
        presenter.updateUpsertUser(model, firebaseId, requireActivity(), typeLogin.toString())

    }

}