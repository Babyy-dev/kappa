package com.kappa.app.core.network.model

data class GooglePlayVerifyRequestDto(
    val packageId: String,
    val productId: String,
    val purchaseToken: String,
    val orderId: String?
)
