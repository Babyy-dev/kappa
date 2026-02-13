package com.kappa.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class RoomEventEnvelope(
    val type: String,
    val roomId: String
)
