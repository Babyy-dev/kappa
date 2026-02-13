package com.kappa.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class RoomMessageRequest(
    val message: String,
    val type: String? = null
)

@Serializable
data class RoomMessageResponse(
    val id: String,
    val roomId: String,
    val userId: String,
    val username: String,
    val message: String,
    val createdAt: Long,
    val type: String = "CHAT"
)

@Serializable
data class GiftSendRequest(
    val recipientId: String? = null,
    val amount: Long,
    val giftId: String? = null,
    val giftType: String? = null,
    val target: String? = null,
    val recipientIds: List<String>? = null
)

@Serializable
data class GiftSendResponse(
    val id: String,
    val roomId: String,
    val senderId: String,
    val recipientId: String? = null,
    val amount: Long,
    val senderBalance: Long,
    val createdAt: Long,
    val giftType: String? = null,
    val diamondsTotal: Long? = null,
    val recipientCount: Int? = null
)
