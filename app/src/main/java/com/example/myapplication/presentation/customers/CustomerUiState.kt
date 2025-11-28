package com.example.myapplication.presentation.customers

import com.example.myapplication.data.remote.dto.CustomerDto

/**
 * Created by H.Mousavioun on 11/28/2025
 */
data class CustomerUiState(
    val customers: List<CustomerDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val newName: String = "",
    val newDescription: String = "",
    val isRefreshing: Boolean = false,
    val editingCustomerId: Long? = null
)
