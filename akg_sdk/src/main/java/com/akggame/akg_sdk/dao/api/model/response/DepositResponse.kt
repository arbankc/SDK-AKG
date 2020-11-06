package com.akggame.akg_sdk.dao.api.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class DepositResponse : BaseResponse() {
    var data: DataBean = DataBean()
    var meta: MetaBean = MetaBean()

    @JsonIgnoreProperties(ignoreUnknown = true)
    class DataBean {
        val id: String = ""
        val transaction_number: String = ""
        val items: String = ""
        val payment_method: String = ""
        val gross_amount: String = ""
        val status: String = ""
        val payment_deadline: String = ""
        val created_at: String = ""
        val updated_at: String = ""
        val deleted_at: String = ""
        val tx_type: String = ""
        val tx_description: String = ""
        val url: String = ""
    }

}