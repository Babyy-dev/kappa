package com.kappa.app.domain.economy

data class CoinPackage(
    val id: String,
    val name: String,
    val coinAmount: Long,
    val priceUsd: String,
    val isActive: Boolean,
    val storeProductId: String?
)
