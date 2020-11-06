package com.akggame.akg_sdk.presenter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.akggame.akg_sdk.AKG_SDK
import com.akggame.akg_sdk.ProductSDKCallback
import com.akggame.akg_sdk.PurchaseSDKCallback
import com.akggame.akg_sdk.dao.BillingDao
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.request.PostOrderRequest
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.GameProductsResponse
import com.akggame.akg_sdk.dao.pojo.PurchaseItem
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.activity.PaymentIView
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import io.reactivex.disposables.Disposable

class ProductPresenter(val mIView: IView) {
    private lateinit var purchaseSDKCallback: PurchaseSDKCallback
    private lateinit var billingDao: BillingDao



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

    fun getProducts(
        gameProvider: String?,
        application: Application,
        context: Context,
        callback: ProductSDKCallback
    ) {
        MainDao().onGetProduct(gameProvider, context)
            .subscribe(object : RxObserver<GameProductsResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    t as GameProductsResponse
                    if (t.meta?.code == 200) {
                        billingDao = BillingDao(
//                            GameProductsResponse().getListOfSKU(t.data),
                            SKU.testListSKU,
                            t.data,
                            this@ProductPresenter,
                            application,
                            object : BillingDao.BillingDaoQuerySKU {
                                override fun onQuerySKU(skuDetails: MutableList<SkuDetails>) {
                                    callback.ProductResult(skuDetails)
                                }
                            }
                        )
                        billingDao.onInitiateBillingClient()
                    } else {
                        (mIView as PaymentIView).handleError("Failed for getting products")
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : "+ e.toString())

                }
            })
    }

    fun lauchBilling(activity: Activity, skuDetails: SkuDetails, callback: PurchaseSDKCallback) {
        billingDao.lauchBillingFlow(activity, skuDetails)

        purchaseSDKCallback = callback
    }

    fun onPostOrder(body: PostOrderRequest, purchase: Purchase, context: Context) {
        MainDao().onPostOrder(body, context)
            .subscribe(object : RxObserver<BaseResponse>(mIView, "") {
                override fun onNext(t: BaseResponse) {
                    super.onNext(t)
                    if (t.BaseMetaResponse?.code == 200) {
                        val purchaseItem = PurchaseItem()
                        purchaseItem.product_id = purchase.sku
                        purchaseItem.product_name = purchase.packageName
                        purchaseSDKCallback.onPurchasedItem(purchaseItem)
                    } else {
                        Toast.makeText(
                            context,
                            "Error: " + t.BaseDataResponse?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onComplete() {
                    super.onComplete()
//                    (mIView as PaymentIView).doOnComplete(purchase)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("TESTING API", "onError : "+ e.toString())

                }
            })
    }
}