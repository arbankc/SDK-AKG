package com.akggame.akg_sdk.dao

import com.google.android.gms.wallet.PaymentMethodTokenizationParameters
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONObject

class PaymentDao() {



    private fun getBaseRequest(): JSONObject {
        return JSONObject()
            .put("apiVersion", 2)
            .put("apiVersionMinor", 0)
    }

    private fun getTokenizationSpecification(): JSONObject {
        val tokenizationSpecification = JSONObject()
        tokenizationSpecification.put("type", "PAYMENT_GATEWAY")
        tokenizationSpecification.put(
            "parameters",
            JSONObject()
                .put("gateway", "www.example.com")
                .put("gatewayMerchantId", "exampleMerchantId")
        )

        return tokenizationSpecification
    }

    private fun getAllowedCardNetworks(): JSONArray {
        return JSONArray()
            .put("MASTERCARD")
            .put("VISA")
    }

    private fun getAllowedCardAuthMethods(): JSONArray {
        return JSONArray()
            .put("PAN_ONLY")
            .put("CRYPTOGRAM_3DS")
    }

    private fun getBaseCardPaymentMethod(): JSONObject {
        val cardPaymentMethod = JSONObject()
        cardPaymentMethod.put("type", "CARD")
        cardPaymentMethod.put(
            "parameters",
            JSONObject()
                .put("allowedAuthMethods", getAllowedCardAuthMethods())
                .put("allowedCardNetworks", getAllowedCardNetworks())
        )
        cardPaymentMethod.put("tokenizationSpecification", getTokenizationSpecification())
        return cardPaymentMethod
    }

    fun getIsReadyToPayRequest(): JSONObject {
        val isReadyToPayRequest = getBaseRequest()
        isReadyToPayRequest.put(
            "allowedPaymentMethods",
            JSONArray()
                .put(getBaseCardPaymentMethod())
        )

        return isReadyToPayRequest
    }

    private fun getCardPaymentMethod(): JSONObject {
        val cardPaymentMethod = getBaseCardPaymentMethod()

        return cardPaymentMethod
    }

    fun getPaymentMethodTokenizationParameters(): PaymentMethodTokenizationParameters.Builder {
        var parameter = PaymentMethodTokenizationParameters.newBuilder()
        parameter.setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_CARD)
            .addParameter("gateway", "example")
            .addParameter("gatewayMerchantId", "exampleGatewayMerchantId")
            .build()
        return parameter
    }

    private fun getTransactionInfo(): JSONObject {
        val transactionInfo = JSONObject()
        transactionInfo.put("totalPrice", "12.34")
        transactionInfo.put("totalPriceStatus", "FINAL")
        transactionInfo.put("currencyCode", "USD")

        return transactionInfo
    }

    private fun getMerchantInfo(): JSONObject {
        return JSONObject()
            .put("merchantName", "Example Merchant")
            .put("merchantId", "01234567890123456789")
    }

    fun getPaymentDataRequest(): JSONObject {
        val paymentDataRequest = getBaseRequest()
        paymentDataRequest.put(
            "allowedPaymentMethods",
            JSONArray()
                .put(getCardPaymentMethod())
        )
        paymentDataRequest.put("transactionInfo", getTransactionInfo())
        paymentDataRequest.put("merchantInfo", getMerchantInfo())

        return paymentDataRequest
    }


}