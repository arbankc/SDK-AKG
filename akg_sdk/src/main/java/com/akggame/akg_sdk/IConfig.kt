package com.akggame.akg_sdk

interface IConfig {

    companion object {
        val BaseUrl: String = "https://akg-api.ptape.com/"
        val BaseUrlDPLK: String = "https://akg-payment-api.ptape.com/"

        val DEFAULT_LOADING = "Loading"
        val TIMEOUT_SHORT_INSECOND = 15
        val TIMEOUT_LONG_INSECOND = 120
        val GOOGLE_CLIENT_ID =
            "138197356819-t0g24ob69ehhtlij4jjkbkfiho8qia76.apps.googleusercontent.com"
        val GOOGLE_CLIENT_ID2 =
            "138197356819-ut0qd2pdub9ak3nuqkv61eqnd5alt6jq.apps.googleusercontent.com"

        val LOGIN_TYPE = "loginType"
        val LOGIN_GOOGLE = "loginGoogle"
        val LOGIN_FACEBOOK = "loginFacebook"
        val LOGIN_PHONE = "loginPhone"
        val LOGIN_GUEST = "loginGuest"

        val SESSION_TOKEN = "auth_token"
        val SESSION_LOGIN = "isLogin"
        val SESSION_USERNAME = "uName"
        val SESSION_UID = "yuaidi"
        val SESSION_GAME = "gemes"
        val SESSION_PIW = "iddong"

        val ADJUST_APP_TOKEN = "app_token"
        val ADJUST_LOGIN = "login"
        val ADJUST_LOGOUT = "logout"
        val ADJUST_PAYMENT_FAILED = "payment_failed"
        val ADJUST_PAYMENT_SUCCESS = "payment_success"
        val ADJUST_REGISTER_SUCCESS = "register_success"
        val ADJUST_REGISTER_FAILED = "register_failed"

        val DPLK_X_API_KEY: String = "Z89xN3LjZIABWRO2nLNTsbUVGYSAKcuTHFnrLWexZV99RguE49mmxlrqblH2"

        val monthLabels = arrayOf(
            "January_Januari",
            "February_Februari",
            "March_Maret",
            "April_April",
            "May_Mei",
            "June_Juni",
            "July_Juli",
            "August_Agustus",
            "September_September",
            "October_Oktober",
            "November_November",
            "December_Desember"
        )
    }
}