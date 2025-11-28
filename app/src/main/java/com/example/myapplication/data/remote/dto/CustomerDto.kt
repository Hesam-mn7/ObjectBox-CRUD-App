package com.example.myapplication.data.remote.dto

import kotlinx.serialization.Serializable


/**
 * Created by H.Mousavioun on 11/28/2025
 */

@Serializable
data class CustomerDto(
    val id: Long? = null,
    val customerName: String,
    val description: String
)

