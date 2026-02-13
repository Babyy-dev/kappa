package com.kappa.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.kappa.app.R
import com.kappa.app.domain.economy.CoinPackage
import com.kappa.app.economy.presentation.EconomyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletFragment : Fragment() {

    private val economyViewModel: EconomyViewModel by viewModels()
    private lateinit var packageAdapter: WalletPackageAdapter
    private var billingClient: BillingClient? = null
    private val productDetailsMap = mutableMapOf<String, ProductDetails>()
    private val packageByProductId = mutableMapOf<String, CoinPackage>()
    private var cachedPackages: List<CoinPackage> = emptyList()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK || purchases == null) {
            return@PurchasesUpdatedListener
        }
        purchases.forEach { handlePurchase(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val balanceText = view.findViewById<TextView>(R.id.text_wallet_balance)
        val statusText = view.findViewById<TextView>(R.id.text_wallet_status)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_wallet_packages)

        packageAdapter = WalletPackageAdapter { coinPackage ->
            launchPurchase(coinPackage)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = packageAdapter

        economyViewModel.loadCoinBalance("me")
        economyViewModel.loadCoinPackages()
        connectBillingClient()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                economyViewModel.viewState.collect { state ->
                    val balance = state.coinBalance?.balance ?: 0
                    balanceText.text = "Balance: $balance coins"
                    statusText.text = state.purchaseMessage ?: state.error.orEmpty()
                    if (state.packages != cachedPackages) {
                        cachedPackages = state.packages
                        packageAdapter.submitList(state.packages)
                        queryProductDetails(state.packages)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        billingClient?.endConnection()
        billingClient = null
        super.onDestroyView()
    }

    private fun connectBillingClient() {
        if (billingClient?.isReady == true) return
        billingClient = BillingClient.newBuilder(requireContext())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails(cachedPackages)
                }
            }

            override fun onBillingServiceDisconnected() {
                // next purchase attempt will retry connection
            }
        })
    }

    private fun queryProductDetails(packages: List<CoinPackage>) {
        val client = billingClient
        if (client == null || !client.isReady) return
        val products = packages.mapNotNull { pkg ->
            pkg.storeProductId?.takeIf { it.isNotBlank() }
        }.distinct()
        if (products.isEmpty()) return
        packageByProductId.clear()
        packages.forEach { pkg ->
            val productId = pkg.storeProductId
            if (!productId.isNullOrBlank()) {
                packageByProductId[productId] = pkg
            }
        }
        val productDetailsList = products.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productDetailsList)
            .build()
        client.queryProductDetailsAsync(params) { billingResult, detailsList ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                return@queryProductDetailsAsync
            }
            productDetailsMap.clear()
            val priceMap = mutableMapOf<String, String>()
            detailsList.forEach { details ->
                productDetailsMap[details.productId] = details
                val formattedPrice = details.oneTimePurchaseOfferDetails?.formattedPrice
                if (!formattedPrice.isNullOrBlank()) {
                    priceMap[details.productId] = formattedPrice
                }
            }
            packageAdapter.updatePrices(priceMap)
        }
    }

    private fun launchPurchase(coinPackage: CoinPackage) {
        val productId = coinPackage.storeProductId
        if (productId.isNullOrBlank()) return
        val details = productDetailsMap[productId] ?: return
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .build()
        val billingParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()
        billingClient?.launchBillingFlow(requireActivity(), billingParams)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            return
        }
        val productId = purchase.products.firstOrNull() ?: return
        val coinPackage = packageByProductId[productId] ?: return
        economyViewModel.verifyGooglePurchase(
            packageId = coinPackage.id,
            productId = productId,
            purchaseToken = purchase.purchaseToken,
            orderId = purchase.orderId
        )
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient?.acknowledgePurchase(params) { }
        }
    }
}
