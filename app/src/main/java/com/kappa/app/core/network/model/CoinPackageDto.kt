package com.kappa.app.core.network.model

data class CoinPackageDto(
    val id: String,
    val name: String,
    val coinAmount: Long,
    val priceUsd: String,
    val isActive: Boolean,
    val storeProductId: String?
)
