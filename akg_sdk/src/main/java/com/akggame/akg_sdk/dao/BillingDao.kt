package com.akggame.akg_sdk.dao

//import android.widget.Toast
//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustEvent
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.ProductData
import com.akggame.akg_sdk.dao.api.model.request.PostOrderRequest
import com.akggame.akg_sdk.presenter.ProductPresenter
import com.akggame.akg_sdk.util.CacheUtil
import com.akggame.akg_sdk.util.Constants
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.google.firebase.analytics.FirebaseAnalytics
import com.orhanobut.hawk.Hawk
import java.io.IOException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*

class BillingDao(
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
    var skuDetails: SkuDetails? = null

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

    private fun querySkuDetailsAsync(
        @BillingClient.SkuType skuType: String,
        skuList: MutableList<String>
    ) {
        val params = SkuDetailsParams.newBuilder()

        params.setSkusList(skuList).setType(skuType)
        billingClient.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            if (billingResult?.responseCode == OK) {
                Log.d(LOG_TAG, "onBillingResultResponseCode is OK")
                if (skuDetailsList != null) {
                    queryCallback.onQuerySKU(skuDetailsList)
                }
            } else {
                Log.e(LOG_TAG, billingResult?.debugMessage.toString())
            }
        }
    }

    fun lauchBillingFlow(activity: Activity, skuDetail: SkuDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetail)
            .build()
        skuDetails = skuDetail
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onBillingServiceDisconnected() {
        Log.d(LOG_TAG, "connectToPlayBillingService")
        connectToPlayBillingService()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        when (billingResult?.responseCode) {
            OK -> {
                Log.d(LOG_TAG, "onBillingSetupFinished successfully")
                querySkuDetailsAsync(BillingClient.SkuType.INAPP, listOfSku.toMutableList())
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                //Some apps may choose to make decisions based on this knowledge.
                Log.e(LOG_TAG, billingResult?.debugMessage.toString())
            }
            else -> {
                //do nothing. Someone else will connect it through retry policy.
                //May choose to send to server though
                Log.e(LOG_TAG, billingResult?.debugMessage.toString())
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

        val (consumables, _) = validPurchases.partition {
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
                println("responBiling result ${billingResult.debugMessage} dan ${billingResult.responseCode}")
                when (billingResult.responseCode) {
                    OK -> {
                        purchaseToken.apply {
                            disburseConsumableEntitlements(it)
                        }
                    }
                    else -> {
                        hitEventFirebase(it, "purchase_failed")
                        Log.w(LOG_TAG, billingResult.debugMessage)
                    }
                }
            }
        }
    }

    private fun disburseConsumableEntitlements(purchase: Purchase) {
        onPaymentSuccess(purchase)
        purchase.sku
    }

    private fun handlePurchase(purchase: Purchase) {
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

//    fun getPrice(datas: List<ProductData>?, sku: String): Double {
//        datas?.forEach {
//            if (it.attributes?.price.equals(sku)) {
//                return it.attributes?.price!!.toDouble()
//            }
//        }
//        return 0.0
//    }


    fun onPaymentSuccess(purchase: Purchase) {
        val idUser = Hawk.get<String>(Constants.DATA_USER_ID)
        val firebaseId = Hawk.get<String>(Constants.FIREBASE_ID)
        val replacePrice = skuDetails!!.price
            .replace("\\s".toRegex(), "")
            .replace("Rp", "")
            .replace(".", "")
            .replace(",", "")

        hitEventFirebase(purchase, "purchase_success")
        val postOrderRequest = PostOrderRequest(
            "Google Play",
            purchase.purchaseTime,
            CacheUtil.getPreferenceString(IConfig.SESSION_GAME, application)!!,
            purchase.orderId,
            purchase.packageName,
            replacePrice.toInt(),
            "Android",
            purchase.sku,
            purchase.purchaseToken,
            purchase.sku,
            "Success",
            CacheUtil.getPreferenceString(IConfig.SESSION_USERNAME, application),
            firebaseId
        )
        presenter.onPostOrder(postOrderRequest, purchase, application)
    }

    private fun hitEventFirebase(purchase: Purchase, eventName: String) {
        val bundle = Bundle()
        val tsLong = System.currentTimeMillis() / 1000
        val timestamp = tsLong.toString()
        bundle.putString("timestamp", timestamp)
        bundle.putString("user_id", CacheUtil.getPreferenceString(IConfig.SESSION_UID, application))
        bundle.putString("item_id", purchase.orderId)
        bundle.putString("currency", skuDetails!!.price)
        bundle.putString("channel", "Google Play")

        val firebaseAnalytics = FirebaseAnalytics.getInstance(application)
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}