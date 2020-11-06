package com.akggame.akg_sdk.dao.pojo

import com.akggame.akg_sdk.ui.dialog.BaseDialogFragment
import com.akggame.akg_sdk.ui.dialog.login.LoginDialogFragment
import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("device_id") val device_id: String,
    @SerializedName("exp") val exp: Int,
    @SerializedName("id") val id: String
) : BaseDialogFragment.JSONConvertable