package com.kappa.app.core.network.model

data class DiamondBalanceDto(
    val userId: String,
    val balance: Long,
    val locked: Long,
    val currency: String = "diamonds"
)
