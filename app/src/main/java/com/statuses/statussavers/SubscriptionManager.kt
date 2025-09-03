package com.statuses.statussavers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.*

object SubscriptionManager : PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    var isSubscribed: Boolean = false
    private const val SUBSCRIPTION_PRODUCT_ID = "year_100" // From Play Console
    var onPurchaseCallback: ((String, Boolean) -> Unit)? = null

    fun initBillingClient(context: Context, onSetupFinished: (Boolean) -> Unit) {
        billingClient = BillingClient.newBuilder(context).setListener(this).enablePendingPurchases().build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onSetupFinished(true)
                }
            }

            override fun onBillingServiceDisconnected() {
                onSetupFinished(false)
            }
        })
    }

    public fun checkSubscription(isSubscribedChecked: (Boolean) -> Unit) {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                isSubscribed = purchasesList.any { purchase ->
                    purchase.products.contains(SUBSCRIPTION_PRODUCT_ID) &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                            !purchase.isAcknowledged.not()
                }
                if (isSubscribed) {
                    isSubscribedChecked(true)
                } else {
                    isSubscribedChecked(false)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)  {
                isSubscribedChecked(true)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_TIMEOUT)  {
                isSubscribedChecked(true)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE)  {
                isSubscribedChecked(true)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR)  {
                isSubscribedChecked(true)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED)  {
                isSubscribedChecked(false)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE)  {
                isSubscribedChecked(false)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE)  {
                isSubscribedChecked(false)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED)  {
                isSubscribedChecked(false)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR)  {
                isSubscribedChecked(true)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ERROR)  {
                isSubscribedChecked(true)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)  {
                isSubscribedChecked(true)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED)  {
                isSubscribedChecked(false)
            } else {
                isSubscribedChecked(true)
            }
        }
    }

    fun launchSubscriptionFlow(activity: Activity) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUBSCRIPTION_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val offerToken = productDetailsList[0].subscriptionOfferDetails?.firstOrNull()?.offerToken
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetailsList[0])
                                .setOfferToken(offerToken!!)
                                .build()
                        )
                    )
                    .build()
                billingClient?.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.products.contains(SUBSCRIPTION_PRODUCT_ID)) {
                    isSubscribed = true
                    if (!purchase.isAcknowledged) {
                        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()

                        billingClient?.acknowledgePurchase(acknowledgeParams) { ackResult ->
                            postCallback("Subscribed Successfully", isSuccessFull = true)
                        }
                    }
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            postCallback("Purchase Cancelled", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)  {
            postCallback("Service Disconnected", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_TIMEOUT)  {
            postCallback("Service Disconnected", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE)  {
            postCallback("Service Unavailable", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR)  {
            postCallback("Network Error", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED)  {
            postCallback("Feature Not Supported", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE)  {
            postCallback("Billing not available in your account", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE)  {
            postCallback("Purchase Failed", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR)  {
            postCallback("Purchase Failed", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ERROR)  {
            postCallback("Purchase Failed", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)  {
            postCallback("Purchase Failed", isSuccessFull = false)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_NOT_OWNED)  {
            postCallback("Purchase Failed", isSuccessFull = false)
        }
        else {
            postCallback("Purchase Failed", isSuccessFull = false)
        }
    }

    private fun postCallback(msg: String, isSuccessFull: Boolean) {
        Handler(Looper.getMainLooper()).post {
            onPurchaseCallback?.let { it(msg, isSuccessFull) }
        }
    }
}
