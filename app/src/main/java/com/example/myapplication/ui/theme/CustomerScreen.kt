package com.example.myapplication.ui.theme


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Customer
import com.example.myapplication.data.CustomerRepository

@Composable
fun CustomerScreen() {
    // Repository را یک‌بار می‌سازیم (با remember)
    val repository = remember { CustomerRepository() }

    // Stateها برای فرم
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // برای اینکه بفهمیم در حال ویرایش کدام مشتری هستیم (یا هیچ‌کدام)
    var selectedCustomerId by remember { mutableStateOf<Long?>(null) }

    // لیست مشتری‌ها که در UI نمایش می‌دهیم
    var customers by remember { mutableStateOf(listOf<Customer>()) }

    // یک تابع کمکی برای رفرش لیست
    fun loadCustomers() {
        customers = repository.getAllCustomers()
    }

    // وقتی کامپوزابل برای اولین بار اجرا شد، لیست را از دیتابیس بخوان
    LaunchedEffect(Unit) {
        loadCustomers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ------------------ فرم بالا ------------------
        Text(text = "Customer Form")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name Customer") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        // می‌توانی بعداً Snackbar یا Toast بگذاری
                        return@Button
                    }

                    if (selectedCustomerId == null) {
                        // CREATE - مشتری جدید
                        repository.addCustomer(name, description)
                    } else {
                        // UPDATE - ویرایش مشتری انتخاب‌شده
                        val customer = Customer(
                            id = selectedCustomerId!!,
                            customerName = name,
                            description = description
                        )
                        repository.updateCustomer(customer)
                    }

                    // بعد از ذخیره، فرم را خالی و لیست را رفرش کن
                    name = ""
                    description = ""
                    selectedCustomerId = null
                    loadCustomers()
                }
            ) {
                Text(
                    text = if (selectedCustomerId == null) "Save (Create)" else "Save (Update)"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    // خالی کردن فرم و خروج از حالت ویرایش
                    name = ""
                    description = ""
                    selectedCustomerId = null
                }
            ) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ------------------ لیست مشتری‌ها ------------------
        Text(text = "Customer List")

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(customers) { customer ->
                CustomerItem(
                    customer = customer,
                    onEditClick = {
                        // وقتی Edit می‌زنیم، اطلاعات بره تو فرم بالا
                        selectedCustomerId = customer.id
                        name = customer.customerName
                        description = customer.description
                    },
                    onDeleteClick = {
                        // حذف از دیتابیس و رفرش لیست
                        repository.deleteCustomer(customer)
                        loadCustomers()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CustomerItem(
    customer: Customer,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // یک ردیف ساده شامل نام و توضیح، و دو دکمه Edit / Delete
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "ID: ${customer.id}")
            Text(text = "Name: ${customer.customerName}")
            Text(text = "Description: ${customer.description}")
        }

        Column {
            TextButton(onClick = onEditClick) {
                Text("Edit")
            }
            TextButton(onClick = onDeleteClick) {
                Text("Delete")
            }
        }
    }
} 

@Preview
@Composable
fun Test() {
    CustomerScreen()
}