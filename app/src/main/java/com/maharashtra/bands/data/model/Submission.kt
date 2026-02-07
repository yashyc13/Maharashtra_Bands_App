package com.maharashtra.bands.data.model

import com.google.firebase.Timestamp

data class Submission(
    val id: String = "",
    val bandName: String = "",
    val city: String = "",
    val type: String = "",
    val phoneNumber: String = "",
    val imageUrl: String = "",
    val status: String = "pending",
    val createdAt: Timestamp? = null
)
