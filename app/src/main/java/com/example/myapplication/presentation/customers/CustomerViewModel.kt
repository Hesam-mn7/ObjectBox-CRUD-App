package com.example.myapplication.presentation.customers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.NetworkModule
import com.example.myapplication.data.remote.dto.CustomerDto
import com.example.myapplication.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by H.Mousavioun on 11/28/2025
 */

private const val TAG = "CUSTOMER_VM"

class CustomerViewModel : ViewModel() {

    private val repository = CustomerRepository(NetworkModule.customerApi)

    private val _uiState = MutableStateFlow(CustomerUiState())
    val uiState: StateFlow<CustomerUiState> = _uiState

    init {
        Log.d(TAG, "ViewModel created → loading customers...")
        loadCustomers()
    }

    // ---------------------------
    // Load (GET /customers)
    // ---------------------------
    fun loadCustomers() {
        viewModelScope.launch {
            Log.d(TAG, "loadCustomers() called")

            _uiState.update { state ->
                state.copy(
                    isLoading = state.customers.isEmpty(),
                    isRefreshing = state.customers.isNotEmpty(),
                    errorMessage = null
                )
            }

            try {
                val result = repository.getCustomers()
                result
                    .onSuccess { list ->
                        Log.d(TAG, "Customers loaded: $list")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                customers = list,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error loading customers", e)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = e.message ?: "Unknown error"
                            )
                        }
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in loadCustomers()", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.message ?: "Unexpected error"
                    )
                }
            }
        }
    }

    // ---------------------------
    // Form handlers
    // ---------------------------
    fun onNameChange(value: String) {
        Log.d(TAG, "Name changed: $value")
        _uiState.update { it.copy(newName = value) }
    }

    fun onDescriptionChange(value: String) {
        Log.d(TAG, "Description changed: $value")
        _uiState.update { it.copy(newDescription = value) }
    }

    // ---------------------------
    // Start / Cancel Edit
    // ---------------------------
    fun startEdit(customer: CustomerDto) {
        Log.d(TAG, "startEdit(): $customer")
        _uiState.update {
            it.copy(
                editingCustomerId = customer.id,
                newName = customer.customerName,
                newDescription = customer.description
            )
        }
    }

    fun cancelEdit() {
        Log.d(TAG, "cancelEdit()")
        _uiState.update {
            it.copy(
                editingCustomerId = null,
                newName = "",
                newDescription = ""
            )
        }
    }

    // ---------------------------
    // Create OR Update (POST / PUT)
    // ---------------------------
    fun createCustomer() {
        val name = _uiState.value.newName.trim()
        val desc = _uiState.value.newDescription.trim()
        val editingId = _uiState.value.editingCustomerId

        if (name.isBlank()) {
            Log.e(TAG, "createCustomer(): name is blank → ignored")
            return
        }

        if (editingId == null) {
            // ------- CREATE -------
            Log.d(TAG, "createCustomer(): CREATE name=$name desc=$desc")

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val dto = CustomerDto(
                    id = null,
                    customerName = name,
                    description = desc
                )

                try {
                    val result = repository.createCustomer(dto)
                    result
                        .onSuccess { created ->
                            Log.d(TAG, "Customer created: $created")

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    customers = it.customers + created,
                                    newName = "",
                                    newDescription = ""
                                )
                            }
                        }
                        .onFailure { e ->
                            Log.e(TAG, "Error creating customer", e)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = e.message ?: "Create failed"
                                )
                            }
                        }

                } catch (e: Exception) {
                    Log.e(TAG, "Exception in createCustomer()", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Unexpected error"
                        )
                    }
                }
            }
        } else {
            // ------- UPDATE -------
            Log.d(TAG, "createCustomer(): UPDATE id=$editingId name=$name desc=$desc")

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val dto = CustomerDto(
                    id = editingId,
                    customerName = name,
                    description = desc
                )

                try {
                    val result = repository.updateCustomer(editingId, dto)
                    result
                        .onSuccess { updated ->
                            Log.d(TAG, "Customer updated: $updated")

                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    customers = state.customers.map { c ->
                                        if (c.id == editingId) updated else c
                                    },
                                    editingCustomerId = null,
                                    newName = "",
                                    newDescription = ""
                                )
                            }
                        }
                        .onFailure { e ->
                            Log.e(TAG, "Error updating customer", e)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = e.message ?: "Update failed"
                                )
                            }
                        }

                } catch (e: Exception) {
                    Log.e(TAG, "Exception in updateCustomer()", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Unexpected error"
                        )
                    }
                }
            }
        }
    }

    // ---------------------------
    // Delete single (DELETE /customers/{id})
    // ---------------------------
    fun deleteCustomer(id: Long) {
        Log.d(TAG, "deleteCustomer(id=$id) called")

        viewModelScope.launch {
            try {
                val result = repository.deleteCustomer(id)
                result
                    .onSuccess {
                        Log.d(TAG, "Customer deleted successfully")

                        _uiState.update { state ->
                            state.copy(
                                customers = state.customers.filterNot { it.id == id }
                            )
                        }
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error deleting customer", e)
                        _uiState.update {
                            it.copy(errorMessage = e.message ?: "Delete failed")
                        }
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in deleteCustomer()", e)
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Unexpected error")
                }
            }
        }
    }

    // ---------------------------
    // Delete ALL (loop روی delete تک‌به‌تک)
    // ---------------------------
    fun deleteAll() {
        Log.d(TAG, "deleteAll() called")

        viewModelScope.launch {
            val current = _uiState.value.customers
            if (current.isEmpty()) {
                Log.d(TAG, "deleteAll(): list is empty → nothing to delete")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                current.forEach { customer ->
                    customer.id?.let { id ->
                        repository.deleteCustomer(id)
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        customers = emptyList()
                    )
                }

                Log.d(TAG, "deleteAll(): all customers deleted")

            } catch (e: Exception) {
                Log.e(TAG, "Exception in deleteAll()", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Delete all failed"
                    )
                }
            }
        }
    }
}
