package com.maharashtra.bands.data.model

import com.google.firebase.Timestamp


data class Submission(
    val id: String = "",
    val bandName: String = "",
    val city: String = "",
    val type: String = "",
    val contactName: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val description: String = "",
    val status: String = "pending",
    val createdAt: Timestamp? = null
)
