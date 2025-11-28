package com.example.myapplication.data

import io.objectbox.Box

class CustomerRepositoryObjectBox {

    // Box همان چیزی است که رویش CRUD انجام می‌دهیم
    private val customerBox: Box<Customer> = ObjectBox.box(Customer::class.java)

    // CREATE → ایجاد مشتری جدید
    fun addCustomer(name: String, description: String): Customer {
        val customer = Customer(
            customerName = name,
            description = description
        )
        // چون id=0 هست، اینجا رکورد جدید ساخته می‌شه
        customerBox.put(customer)
        return customer
    }

    // READ → خواندن همه مشتری‌ها
    fun getAllCustomers(): List<Customer> {
        return customerBox.all
    }

    // UPDATE → با گرفتن یک Customer
    fun updateCustomer(customer: Customer) {
        // اگر customer.id موجود باشه، put نقش Update رو بازی می‌کنه
        customerBox.put(customer)
    }

    // DELETE → حذف با خود آبجکت
    fun deleteCustomer(customer: Customer) {
        customerBox.remove(customer)
    }

    // DELETE → حذف با id
    fun deleteCustomerById(id: Long) {
        customerBox.remove(id)
    }

    // حذف همه مشتری‌ها (اختیاری)
    fun deleteAll() {
        customerBox.removeAll()
    }
}