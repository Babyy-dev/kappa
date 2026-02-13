package com.kappa.app.economy.domain.usecase

import com.kappa.app.domain.economy.CoinPackage
import com.kappa.app.economy.domain.repository.EconomyRepository
import javax.inject.Inject

class GetCoinPackagesUseCase @Inject constructor(
    private val economyRepository: EconomyRepository
) {
    suspend operator fun invoke(): Result<List<CoinPackage>> {
        return economyRepository.getCoinPackages()
    }
}
