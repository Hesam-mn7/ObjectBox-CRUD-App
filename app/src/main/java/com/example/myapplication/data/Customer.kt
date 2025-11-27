package com.example.myapplication.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Customer(
    @Id var id: Long = 0,
    var customerName: String = "",
    var description: String = ""
)
