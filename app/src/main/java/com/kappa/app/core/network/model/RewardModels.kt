package com.kappa.app.core.network.model

data class RewardRequestDto(
    val id: String,
    val userId: String,
    val diamonds: Long,
    val status: String,
    val createdAt: Long,
    val processedAt: Long? = null,
    val note: String? = null
)

data class RewardRequestCreateDto(
    val diamonds: Long
)
