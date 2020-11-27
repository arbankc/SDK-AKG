package com.akggame.akg_sdk.ui.dialog.menu

import com.akggame.akg_sdk.dao.api.model.response.GameListResponse

interface GameListIView {
    fun doOnSuccessGameList(gameListResponse: GameListResponse)
    fun doOnErrorGameList(message: String?)
    fun doOnLoadingGameList(isShow: Boolean)
}