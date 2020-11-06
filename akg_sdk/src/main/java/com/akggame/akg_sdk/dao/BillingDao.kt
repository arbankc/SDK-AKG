package com.akggame.akg_sdk.dao

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
//import android.widget.Toast
//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustEvent
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.api.model.request.PostOrderRequest
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.akggame.akg_sdk.presenter.ProductPresenter
import com.akggame.akg_sdk.util.CacheUtil
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.IOException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.HashSet

class BillingDao constructor(
    private val listOfSku: List<String>,
    private val productData: List<ProductData>,
    private val presenter: ProductPresenter,
    private val application: Application,
    val queryCallback: BillingDaoQuerySKU
) :
    PurchasesUpdatedListener, BillingClientStateListener {


    val LOG_TAG = "Billing Dao :"
    lateinit var billingClient: BillingClient

    private val BASE_64_ENCODED_PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtigMU9IVSI89hggyB7DKj9W0ROgAHaBPjv9o5mfeMaSg1Js9P12Ch6FTkCP6iyx5cbK1a0DkmW12cEGe12MtCCY93xs3AY9HZiFemzgS2VyaMZiCMc3NU1dYWSPjiemmfzI0mI33IzEt/67vzgXqev03WsrSKckcPXt2KoahDxHNTr8CcGd44mIWWxHCfawd95lAM19/bf8mvNDT84pUz7nLGt8FzC7IAn55h/+8keXu9hhRzT3511KlUx2Yp3sHsfac/lKlsrSZFmsxRbrSgFWslMxtx3lhggOL1XN4qcr6qsfrKkA9dKndvEktA85DFYHnq9ETDvGBYMUJx24MnQIDAQAB"
    private val KEY_FACTORY_ALGORITHM = "RSA"
    private val SIGNATURE_ALGORITHM = "SHA1withRSA"
    private val TAG = "VERIFY PAYMENT"
    private val firebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(application)
    }

    object SKU {
        val janjiDoang = "com.sdkgame.product1"
        val tempeOrek = "com.sdkgame.product2"
        val janjiDoang2 = "com.sdkgame.product1"
        val tempeOrek2 = "com.sdkgame.product2"
        val product3 = "com.sdkgame.product3"

        val testingPurchased = "android.test.purchased"
        val testingCancelled = "android.test.canceled"
        val testingUnavailable = "android.test.item_unavailable"

        val myTestListSKU = listOf(janjiDoang2, tempeOrek2, product3)

        val myListSKU = listOf(janjiDoang, tempeOrek)

        val testListSKU = listOf(testingPurchased, testingCancelled, testingUnavailable)
    }

    interface BillingDaoQuerySKU {
        fun onQuerySKU(skuDetails: MutableList<SkuDetails>)
    }

    interface PaymentResponse {
        fun onPaymentSuccess(purchases: Purchase)
        fun onPaymentFailed()
    }

    fun onInitiateBillingClient() {
        billingClient = BillingClient.newBuilder(application.applicationContext)
            .setListener(this@BillingDao)
            .enablePendingPurchases()
            .build()
        connectToPlayBillingService()
    }

    private fun connectToPlayBillingService(): Boolean {
        Log.d(LOG_TAG, "connectToPlayBillingService")
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
            return true
        }
        return false
    }

    fun querySkuDetailsAsync(@BillingClient.SkuType skuType: String, skuList: List<String>) {

        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(skuType)
            .build()

        billingClient.querySkuDetailsAsync(params, object : SkuDetailsResponseListener {
            override fun onSkuDetailsResponse(
                billingResult: BillingResult?,
                skuDetailsList: MutableList<SkuDetails>?
            ) {
                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(LOG_TAG, "onBillingResultResponseCode is OK")
                    if (skuDetailsList != null) {
                        queryCallback.onQuerySKU(skuDetailsList)
                    }
                } else {
                    Log.e(LOG_TAG, billingResult?.debugMessage)
                    Log.e(LOG_TAG, billingResult?.debugMessage.toString())
                }
            }

        })
    }

    fun lauchBillingFlow(activity: Activity, skuDetails: SkuDetails) {
        var billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onBillingServiceDisconnected() {
        Log.d(LOG_TAG, "connectToPlayBillingService")
        connectToPlayBillingService()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(LOG_TAG, "onBillingSetupFinished successfully")
                querySkuDetailsAsync(BillingClient.SkuType.INAPP, listOfSku)
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                //Some apps may choose to make decisions based on this knowledge.
                Log.d(LOG_TAG, billingResult.debugMessage)
            }
            else -> {
                //do nothing. Someone else will connect it through retry policy.
                //May choose to send to server though
                Log.d(LOG_TAG, billingResult?.debugMessage)
            }
        }
    }

    fun queryPurchasesAsync() {
        Log.d(LOG_TAG, "queryPurchasesAsync called")
        val purchasesResult = HashSet<Purchase>()
        var result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        Log.d(LOG_TAG, "queryPurchasesAsync INAPP results: ${result?.purchasesList?.size}")
        result?.purchasesList?.apply { purchasesResult.addAll(this) }
        processPurchase(purchasesResult)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult?.responseCode == OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
                purchases.apply {
                    processPurchase(this.toSet())
                };
            }
        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {

        } else {

        }
    }

    fun processPurchase(purchasesResult: Set<Purchase>) {
        val validPurchases = HashSet<Purchase>(purchasesResult.size)
        purchasesResult.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                validPurchases.add(purchase)
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Log.d(LOG_TAG, "Received a pending purchase of SKU: ${purchase.sku}")
            }
        }

        val (consumables, nonConsumables) = validPurchases.partition {
            listOfSku.contains(it.sku)
        }

        handleConsumablePurchasesAsync(consumables)

    }

    private fun handleConsumablePurchasesAsync(consumables: List<Purchase>) {
        Log.d(LOG_TAG, "handleConsumablePurchasesAsync called")
        consumables.forEach {
            Log.d(LOG_TAG, "handleConsumablePurchasesAsync foreach it is $it")

            val params = ConsumeParams.newBuilder()
                .setPurchaseToken(it.purchaseToken)
                .build()

            billingClient.consumeAsync(params) { billingResult, purchaseToken ->
                when (billingResult.responseCode) {
                    OK -> {
                        purchaseToken.apply { disburseConsumableEntitlements(it) }
                    }
                    else -> {
                        Log.w(LOG_TAG, billingResult.debugMessage)
                    }
                }
            }
        }
    }

    fun disburseConsumableEntitlements(purchase: Purchase) {
        onPaymentSuccess(purchase)
        purchase.sku
    }

    fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {

        }
    }

    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        val signatureBytes: ByteArray
        try {
            signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Base64 decoding failed.")
            return false
        }
        try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            if (!signatureAlgorithm.verify(signatureBytes)) {
                Log.w(TAG, "Signature verification failed...")
                return false
            }
            return true
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            Log.w(TAG, "Invalid key specification.")
        } catch (e: SignatureException) {
            Log.w(TAG, "Signature exception.")
        }
        return false
    }

    private fun isSignatureValid(purchase: Purchase): Boolean {
        return verifyPurchase(
            BASE_64_ENCODED_PUBLIC_KEY, purchase.originalJson, purchase.signature
        )
    }

    @Throws(IOException::class)
    fun verifyPurchase(base64PublicKey: String, signedData: String, signature: String): Boolean {
        if ((TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey)
                    || TextUtils.isEmpty(signature))
        ) {
            Log.w("VERIFY PURCHASE", "Purchase verification failed: missing data.")
            return false
        }
        val key = generatePublicKey(base64PublicKey)
        return verify(key, signedData, signature)
    }

    @Throws(IOException::class)
    private fun generatePublicKey(encodedPublicKey: String): PublicKey {
        try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            val msg = "Invalid key specification: $e"
            Log.w("VERIFY PAYMENT", msg)
            throw IOException(msg)
        }
    }

    fun getPrice(datas: List<ProductData>?, sku: String): Double {
        datas?.forEach {
            if (it.attributes?.product_code.equals(sku)) {
                return it.attributes?.price!!.toDouble()
            }
        }
        return 0.0
    }

    fun onPaymentFailed() {
//        setAdjustEventPaymentFailed()
    }

    fun onPaymentSuccess(purchase: Purchase) {
        setAdjustEventPaymentSuccess(getPrice(productData, purchase.sku), purchase.sku)
        val postOrderRequest = PostOrderRequest(
            "Google Play",
            purchase.purchaseTime,
            CacheUtil.getPreferenceString(IConfig.SESSION_GAME, application)!!,
            purchase.orderId,
            purchase.packageName,
            3000,
            "Android",
            "com.sdkgame.product1",
            purchase.purchaseToken,
            purchase.sku,
            "Success",
            CacheUtil.getPreferenceString(IConfig.SESSION_UID, application),
            CacheUtil.getPreferenceString(IConfig.SESSION_USERNAME, application)
        )
        presenter.onPostOrder(postOrderRequest, purchase, application)

    }

    fun setAdjustEventPaymentSuccess(price: Double, sku: String) {
        if (CacheUtil.getPreferenceString(IConfig.ADJUST_PAYMENT_SUCCESS, application) != null) {
//            val adjustEvent =
//                AdjustEvent(
//                    CacheUtil.getPreferenceString(
//                        IConfig.ADJUST_PAYMENT_SUCCESS,
//                        application
//                    )
//                )
//            adjustEvent.setRevenue(price, "IDR")
//            adjustEvent.setOrderId(sku)
//            adjustEvent.addCallbackParameter("user_id",CacheUtil.getPreferenceString(IConfig.SESSION_PIW,application))
//            Adjust.trackEvent(adjustEvent)

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.CURRENCY,"IDR")
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID,sku)
            bundle.putString("user_id",CacheUtil.getPreferenceString(IConfig.SESSION_PIW,application))
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE,bundle)
        }
    }

    fun setAdjustEventPaymentFailed(sku: String) {
//        if (CacheUtil.getPreferenceString(IConfig.ADJUST_PAYMENT_FAILED, application) != null) {
//            val adjustEvent = AdjustEvent(CacheUtil.getPreferenceString(IConfig.ADJUST_PAYMENT_FAILED, application))
//            adjustEvent.addCallbackParameter("user_id",CacheUtil.getPreferenceString(IConfig.SESSION_PIW,application))
//            Adjust.trackEvent(adjustEvent)
//
//        }
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY,"IDR")
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,sku)
        bundle.putString("user_id",CacheUtil.getPreferenceString(IConfig.SESSION_PIW,application))
        firebaseAnalytics.logEvent("purchase_failed",bundle)
    }
}