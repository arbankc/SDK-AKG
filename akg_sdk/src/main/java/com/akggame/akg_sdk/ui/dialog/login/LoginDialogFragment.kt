package com.akggame.akg_sdk.ui.dialog.login

//import com.akggame.akg_sdk.ui.BaseActivity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.LoginSDKCallback
import com.akggame.akg_sdk.`interface`.OnClickItem
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList
import com.akggame.akg_sdk.dao.api.model.response.GameListResponse
import com.akggame.akg_sdk.presenter.GamePresenter
import com.akggame.akg_sdk.presenter.LoginPresenter
import com.akggame.akg_sdk.ui.adapter.GameListAdapter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.PhoneLoginDialogFragment
import com.akggame.akg_sdk.ui.dialog.menu.GameListIView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.content_dialog_login.*
import kotlinx.android.synthetic.main.content_dialog_login.view.*
import org.json.JSONException


class LoginDialogFragment() : BaseDialogFragment(), LoginIView, GameListIView {

    lateinit var mView: View
    lateinit var callbackManager: CallbackManager
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
    var productCodeGame: String? = null

    var emailFb: String? = null
    var nameFb: String? = null
    var tokenFb: String? = null


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

//        if (currentUser != null) {
////            showToast("onContinue")
//        } else showToast("Belun login")

    }


    override fun doOnSuccess(
        isFirstLogin: Boolean,
        token: String,
        userId: String,
        typeLogin: String
    ) {
//        val id = DeviceUtil.decoded(token).toObject<UserData>()
        mLoginCallback.onResponseSuccess(token, "", typeLogin)
        CacheUtil.putPreferenceString(IConfig.SESSION_PIW, "", requireActivity())
        SocmedDao.setAdjustEventLogin(isFirstLogin, requireActivity())
        dismiss()
        showDialogLoading(false)
        Hawk.put(Constants.DATA_USER_ID, userId)
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
                activity?.let { presenterGame.onGameList(it) }
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
//                loginUpdateGuestFirebase()
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
                        activity?.let { presenterGame.onGameList(it) }
                    } else {
                        // If sign in fails, display a message to the user.
                        showToast("Gagal geuest")
                    }

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
            idToken = account?.idToken
            d { "respon Login account ${account?.idToken}" }
            firebaseAuthWithGoogle(account?.idToken.toString())
        } catch (e: ApiException) {
            Log.w("FRAGMENT_GOOGLE", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser = auth?.currentUser
                    d { "respon Login succes ${currentUser?.email}" }
                    statusLogin = "google"
                    activity?.let { presenterGame.onGameList(it) }
                } else {
                    d { "respon Login failed " }

                }
            }

    }

    val onClickItem: OnClickItem = object : OnClickItem {
        override fun clickItem(pos: Int) {
            val itemGameList = dataItemGameList?.get(pos)
            productCodeGame = itemGameList?.attributes?.productCode
        }
    }

    private fun showDialogListGame(dataItemGameList: List<DataItemGameList>?) {
        val rvGameList = dialogListGame?.findViewById<RecyclerView>(R.id.rvListGame)
        val btnStart = dialogListGame?.findViewById<Button>(R.id.btnStartGame)
        dialogListGame?.show()

//        val groupieAdapterMenu = GroupAdapter<ViewHolder>().apply {
//            dataItemGameList?.forEach {
//                add(MenuListGameAdapter(onClickItem, it))
//            }
//
//            notifyDataSetChanged()
//        }

        val gameListAdapter = GameListAdapter()
        gameListAdapter.dataItemGameList = dataItemGameList as MutableList<DataItemGameList>?
        rvGameList?.apply {
            adapter = gameListAdapter
            layoutManager = GridLayoutManager(activity, 3)
            hasFixedSize()
        }
        gameListAdapter.onClickItem = onClickItem
        productCodeGame =
            gameListAdapter.dataItemGameList?.get(0)?.attributes?.productCode.toString()

        btnStart?.setOnClickListener {
            if (productCodeGame?.isNotEmpty()!!) {
                when {
                    statusLogin.equals("facebook", ignoreCase = true) -> {
                        hitLogin(
                            emailFb,
                            nameFb,
                            tokenFb,
                            statusLogin
                        )
                    }
                    statusLogin.equals("google", ignoreCase = true) -> {
                        hitLogin(
                            currentUser?.email,
                            currentUser?.displayName,
                            idToken.toString(),
                            statusLogin
                        )
                    }

                    statusLogin.equals("guest", ignoreCase = true) -> {
                        hitLogin("user@gmail.com", "user-guest", "", statusLogin)
                    }


                }
            } else showToast("Silahkan pilih game dahulu")


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


    private fun hitLogin(email: String?, name: String?, accesToken: String?, typeLogin: String?) {
        showDialogLoading(true)
        val model = FacebookAuthRequest()
        model.email = email
        model.access_token = accesToken
        model.name = name
        model.phone_number = "-"
        model.auth_provider = "akg"

        val android_id: String = Settings.Secure.getString(
            activity?.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        model.device_id = android_id
        model.game_provider = productCodeGame
        model.operating_system = "Android"
        model.phone_model = DeviceUtil.getDeviceName()
        model.login_type = typeLogin

        activity?.let {
            CacheUtil.putPreferenceString(
                IConfig.SESSION_GAME, productCodeGame.toString(),
                it
            )
        }

        typeLogin(typeLogin)

        Hawk.put(Constants.DATA_UPSERT, model)
        dialogListGame?.dismiss()

        LoginPresenter(this@LoginDialogFragment)
            .upsertUser(model, requireActivity(), typeLogin.toString())
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


    override fun doOnSuccessGameList(gameListResponse: GameListResponse) {
        dataItemGameList = gameListResponse.data as List<DataItemGameList>?
        showDialogListGame(dataItemGameList)

    }

    override fun doOnErrorGameList(message: String?) {
        d { "respon Error gamelist $message" }
        showDialogLoading(false)
    }

    override fun doOnLoadingGameList(isShow: Boolean) {
        showDialogLoading(isShow)
    }


}