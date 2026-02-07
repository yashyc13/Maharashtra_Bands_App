package com.maharashtra.bands.data.model

import com.google.firebase.Timestamp


data class Band(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val type: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isApproved: Boolean = false,
    val createdAt: Timestamp? = null
)
