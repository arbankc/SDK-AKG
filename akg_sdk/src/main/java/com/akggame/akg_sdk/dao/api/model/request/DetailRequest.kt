package com.clappingape.dplkagent.model.api.request


data class DepositRequest(
    var account_id: String = "",
    var payment_method_id: String = "",
    var username: String = "",
    var email: String = "",
    var phone: String = "",
    var amount: Int = 0,
    var registered_number: String = ""
)