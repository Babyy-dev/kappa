package com.kappa.app.economy.domain.repository

import com.kappa.app.domain.economy.CoinBalance
import com.kappa.app.domain.economy.CoinPackage
import com.kappa.app.domain.economy.CoinPurchase
import com.kappa.app.domain.economy.Transaction

/**
 * Economy repository interface.
 */
interface EconomyRepository {
    suspend fun getCoinBalance(userId: String): Result<CoinBalance>
    suspend fun getTransactions(userId: String): Result<List<Transaction>>
    suspend fun getCoinPackages(): Result<List<CoinPackage>>
    suspend fun verifyGooglePurchase(
        packageId: String,
        productId: String,
        purchaseToken: String,
        orderId: String?
    ): Result<CoinPurchase>
}
