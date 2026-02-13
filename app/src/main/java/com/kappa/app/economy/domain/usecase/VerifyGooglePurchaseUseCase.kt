package com.kappa.app.economy.domain.usecase

import com.kappa.app.domain.economy.CoinPurchase
import com.kappa.app.economy.domain.repository.EconomyRepository
import javax.inject.Inject

class VerifyGooglePurchaseUseCase @Inject constructor(
    private val economyRepository: EconomyRepository
) {
    suspend operator fun invoke(
        packageId: String,
        productId: String,
        purchaseToken: String,
        orderId: String?
    ): Result<CoinPurchase> {
        return economyRepository.verifyGooglePurchase(packageId, productId, purchaseToken, orderId)
    }
}
