package com.akggame.akg_sdk

import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList

interface StartGameSDKCallback {
    fun onStartGame(dataItemGameList: DataItemGameList)

}