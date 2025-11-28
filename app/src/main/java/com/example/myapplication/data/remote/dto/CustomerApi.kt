package com.example.myapplication.data.remote.dto

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Created by H.Mousavioun on 11/28/2025
 */
interface CustomerApi {
    @GET("customers")
    suspend fun getCustomers(): List<CustomerDto>

    @GET("customers/{id}")
    suspend fun getCustomer(
        @Path("id") id: Long
    ): CustomerDto

    @POST("customers")
    suspend fun createCustomer(
        @Body dto: CustomerDto
    ): CustomerDto

    @PUT("customers/{id}")
    suspend fun updateCustomer(
        @Path("id") id: Long,
        @Body dto: CustomerDto
    ): CustomerDto

    @DELETE("customers/{id}")
    suspend fun deleteCustomer(
        @Path("id") id: Long
    )
}