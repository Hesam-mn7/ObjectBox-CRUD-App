package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.CustomerApi
import com.example.myapplication.data.remote.dto.CustomerDto

/**
 * Created by H.Mousavioun on 11/28/2025
 */
class CustomerRepository(
    private val api: CustomerApi
) {
    suspend fun getCustomers(): Result<List<CustomerDto>> =
        runCatching { api.getCustomers() }

    suspend fun createCustomer(dto: CustomerDto): Result<CustomerDto> =
        runCatching { api.createCustomer(dto) }

    suspend fun updateCustomer(id: Long, dto: CustomerDto): Result<CustomerDto> =
        runCatching { api.updateCustomer(id, dto) }

    suspend fun deleteCustomer(id: Long): Result<Unit> =
        runCatching { api.deleteCustomer(id) }
}