package com.akggame.akg_sdk.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.LoginSDKCallback
import com.akggame.akg_sdk.`interface`.OnClickItem
import com.akggame.akg_sdk.dao.SocmedDao
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList
import com.akggame.akg_sdk.dao.api.model.response.FacebookAuthResponse
import com.akggame.akg_sdk.dao.api.model.response.GameListResponse
import com.akggame.akg_sdk.presenter.GamePresenter
import com.akggame.akg_sdk.presenter.LoginPresenter
import com.akggame.akg_sdk.ui.adapter.GameListAdapter
import com.akggame.akg_sdk.ui.dialog.login.LoginIView
import com.akggame.akg_sdk.ui.dialog.menu.GameListIView
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.akggame.akg_sdk.util.DeviceUtil
import com.akggame.android.sdk.R
import com.github.ajalt.timberkt.d
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_game_list_dialog.*

/**
 * A simple [Fragment] subclass.
 * Use the [GameListDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameListDialogFragment : BaseDialogFragment(), GameListIView, LoginIView {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var productCodeGame: String? = null
    var gameIdProduct: String? = null

    var dataItemGameList: List<DataItemGameList>? = null
    var itemGameList: DataItemGameList? = null
    var statusLogin: String? = null


    var emailFb: String? = null
    var nameFb: String? = null
    var tokenFb: String? = null
    var idFb: String? = null

    var firebaseUser: FirebaseUser? = null
    var firebaseAuth: FirebaseAuth? = null
    var statusAuth: String? = null

    val onClickItem: OnClickItem = object : OnClickItem {
        override fun clickItem(pos: Int) {
            itemGameList = dataItemGameList?.get(pos)
            productCodeGame = itemGameList?.attributes?.productCode
            gameIdProduct = itemGameList?.id.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }

        val style = DialogFragment.STYLE_NO_FRAME
        setStyle(style, theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser

        initial()
    }

    private fun initial() {
        GamePresenter(this)
            .onGameList(context as Context)

    }

    private fun showDialogListGame(dataItemGameList: List<DataItemGameList>?) {
        val gameListAdapter = GameListAdapter()
        gameListAdapter.dataItemGameList = dataItemGameList as MutableList<DataItemGameList>?
        rvListGame?.apply {
            adapter = gameListAdapter
            layoutManager = GridLayoutManager(activity, 3)
            hasFixedSize()
        }
        gameListAdapter.onClickItem = onClickItem

        productCodeGame =
            gameListAdapter.dataItemGameList?.get(0)?.attributes?.productCode.toString()
        gameIdProduct = gameListAdapter.dataItemGameList?.get(0)?.id.toString()
        Hawk.put(Constants.ID_GAME, gameIdProduct)
        btnStartGame?.setOnClickListener {
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
                            firebaseUser?.email,
                            firebaseUser?.displayName,
                            "",
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
        Hawk.put("gameId", gameIdProduct)


        itemGameList?.let { it1 ->
            AKG_SDK.getStartSdkCallback()?.onStartGame(it1)
        }


        LoginPresenter(this)
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


    private fun hitLogEventFirebase(
        eventName: String,
        typeLogin: String,
        userId: String,
        uid: String
    ) {
        val bundle = Bundle()

        bundle.putString("UID", uid)
        bundle.putString("UserId", userId)
        bundle.putString("Timestamp", createTimestamp())
        bundle.putString("Type_Login", typeLogin)

        hitEventFirebase(eventName, bundle)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private lateinit var mLoginCallback: LoginSDKCallback

        fun newInstance(
            statusLogin: String?,
            emailFb: String?,
            loginCallback: LoginSDKCallback,
            namaFb: String?,
            statusSucces: String?,
            firebaseUser: FirebaseUser
        ): GameListDialogFragment {
            val fragment = GameListDialogFragment()
            fragment.statusLogin = statusLogin
            fragment.emailFb = emailFb
            fragment.nameFb = namaFb
            fragment.firebaseUser = firebaseUser
            fragment.statusAuth = statusSucces
            val args = Bundle()
            mLoginCallback = loginCallback
            fragment.arguments = args
            return fragment
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

    override fun doOnSuccess(
        facebookAuthResponse: FacebookAuthResponse.DataBean?,
        isFirstLogin: Boolean,
        token: String,
        userId: String,
        typeLogin: String
    ) {

        val baseOnBanned = facebookAuthResponse?.attributes?.banned_based_on

        if (baseOnBanned != null) {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setMessage(facebookAuthResponse.attributes!!.banned_message)
            alertDialog.setPositiveButton(
                "Ok"
            ) { p0, p1 -> p0?.dismiss() }
            alertDialog.create().show()
        } else {
            mLoginCallback.onResponseSuccess(token, "", typeLogin)
            CacheUtil.putPreferenceString(IConfig.SESSION_PIW, "", requireActivity())
            SocmedDao.setAdjustEventLogin(isFirstLogin, requireActivity())
            Hawk.put(Constants.DATA_USER_ID, userId)
            facebookAuthResponse?.let { validateEventNameType(it, typeLogin) }
        }

        dismiss()
        showDialogLoading(false)

    }

    private fun validateEventNameType(
        facebookAuthResponse: FacebookAuthResponse.DataBean,
        typeLogin: String
    ) {
        val eventName = facebookAuthResponse?.attributes?.event_name.toString()
        if (statusAuth.equals("sukses", ignoreCase = true)
        ) {
            if (eventName.equals("login", ignoreCase = true)) {
                hitLogEventFirebase(
                    eventName,
                    typeLogin,
                    facebookAuthResponse?.id.toString(),
                    facebookAuthResponse?.attributes?.uid.toString()
                )
            } else if (eventName.equals("register", ignoreCase = true)) {
                hitLogEventFirebase(
                    "register_succes",
                    typeLogin,
                    facebookAuthResponse?.id.toString(),
                    facebookAuthResponse?.attributes?.uid.toString()
                )
            }
        } else if (statusAuth.equals("failed", ignoreCase = true)) {
            if (eventName.equals("register", ignoreCase = true)) {
                hitLogEventFirebase(
                    "register_failed",
                    typeLogin,
                    facebookAuthResponse?.id.toString(),
                    facebookAuthResponse?.attributes?.uid.toString()
                )
            }

        }
    }

    override fun doOnError(message: String) {
        mLoginCallback.onResponseFailed(message)
        customDismiss()
        clearBackStack()
    }

}