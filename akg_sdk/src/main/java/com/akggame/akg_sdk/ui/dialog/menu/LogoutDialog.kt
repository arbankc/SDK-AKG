package com.akggame.akg_sdk.ui.dialog.menu

//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustEvent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.callback.LogoutSdkCallback
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.presenter.LogoutPresenter
import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.newandroid.sdk.R
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.content_dialog_logout.*
import kotlinx.android.synthetic.main.content_dialog_logout.view.*

class LogoutDialog() : BaseDialogFragment(), LogoutIView {

    lateinit var mView: View
    val presenter = LogoutPresenter(this)
    var auth: FirebaseAuth? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient

    constructor(fm: FragmentManager?) : this() {
        myFragmentManager = fm
    }

    companion object {
        internal lateinit var logoutSdkCallback: LogoutSdkCallback

        fun newInstance(callback: LogoutSdkCallback): LogoutDialog {
            val mDialogFragment = LogoutDialog()
            logoutSdkCallback = callback
            return mDialogFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.content_dialog_logout, container, true)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        mGoogleSignInClient = SocmedDao.setGoogleSigninClient(requireContext())

    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    override fun doSuccess() {
        this.dismiss()
        setAdjustEventLogout()
    }

    override fun doError() {
        Toast.makeText(requireActivity(), "Error logout", Toast.LENGTH_LONG).show()
    }

    fun initialize() {
        mView.ivClose.setOnClickListener {
            this.dismiss()
        }
        btnNext.setOnClickListener {
            if (CacheUtil.getPreferenceBoolean(IConfig.SESSION_LOGIN, requireActivity())) {
                hitEventLogout()
                signOut()
            } else {
                Toast.makeText(requireActivity(), "You are not logged in", Toast.LENGTH_LONG).show()
            }
        }

        btnBack.setOnClickListener {
            this.dismiss()
        }
    }

    fun hitEventLogout() {
        val packageName = Hawk.get<String>(Constants.ID_GAME_PROVIDER)
        val bundle = Bundle()
        bundle.putString("game_provider", packageName)
        bundle.putString("uid", getDataLogin()?.data?.attributes?.firebase_id)
        bundle.putString("udid", deviceIdAndroid())
        hitEventFirebase(Constants.EVENT_LOGOUT, bundle)
    }

    private fun signOut() {
        showDialogLoading(true)
        auth?.signOut()
        activity?.let {
            mGoogleSignInClient.signOut().addOnCompleteListener(it) {
                showDialogLoading(false)
                Hawk.deleteAll()
                CacheUtil.clearPreference(activity!!)
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                }
                logoutSdkCallback.onSuccesLogout()

            }
        }
    }

    fun setAdjustEventLogout() {
        val bundle = Bundle()
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        bundle.putString(
            "user_id",
            CacheUtil.getPreferenceString(IConfig.SESSION_PIW, requireActivity())
        )
        bundle.putString("timestamp", createTimestamp())
        firebaseAnalytics.logEvent("logout", bundle)
    }

}