package com.example.myapplication.presentation.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.remote.dto.CustomerDto

/**
 * Created by H.Mousavioun on 11/28/2025
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(
    viewModel: CustomerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customers (ObjectBox Server)") },
                actions = {
                    IconButton(onClick = { viewModel.loadCustomers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(
                        onClick = { viewModel.deleteAll() },
                        enabled = state.customers.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete all")
                    }
                }
            )
        }
    ) { padding ->

        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.loadCustomers() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // فرم اضافه کردن / ویرایش
                OutlinedTextField(
                    value = state.newName,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Customer Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.newDescription,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    if (state.editingCustomerId != null) {
                        TextButton(onClick = { viewModel.cancelEdit() }) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                    }

                    Button(
                        onClick = { viewModel.createCustomer() },
                        enabled = !state.isLoading && state.newName.isNotBlank()
                    ) {
                        Text(
                            if (state.editingCustomerId == null) "Add"
                            else "Save"
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (state.isLoading && state.customers.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    CustomerList(
                        customers = state.customers,
                        onDelete = { id -> viewModel.deleteCustomer(id) },
                        onEdit = { customer -> viewModel.startEdit(customer) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerList(
    customers: List<CustomerDto>,
    onDelete: (Long) -> Unit,
    onEdit: (CustomerDto) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(customers, key = { it.id ?: 0L }) { customer ->
            CustomerItem(
                customer = customer,
                onDelete = onDelete,
                onEdit = onEdit
            )
            Divider()
        }
    }
}

@Composable
private fun CustomerItem(
    customer: CustomerDto,
    onDelete: (Long) -> Unit,
    onEdit: (CustomerDto) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            Text(
                text = customer.customerName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (customer.description.isNotBlank()) {
                Text(
                    text = customer.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        val id = customer.id
        if (id != null) {
            Row {
                TextButton(onClick = { onEdit(customer) }) {
                    Text("Edit")
                }
                TextButton(onClick = { onDelete(id) }) {
                    Text("Delete")
                }
            }
        }
    }
}
