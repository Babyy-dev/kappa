package com.kappa.app.core.network.model

data class AnnouncementDto(
    val id: String,
    val title: String,
    val message: String,
    val isActive: Boolean
)
