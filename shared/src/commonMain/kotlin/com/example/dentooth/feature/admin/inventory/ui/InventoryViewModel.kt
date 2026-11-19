package com.example.dentooth.feature.admin.inventory.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.admin.inventory.data.InventoryRepository
import com.example.dentooth.feature.admin.inventory.domain.model.InventoryCategory
import com.example.dentooth.feature.admin.inventory.domain.model.InventoryItem
import com.example.dentooth.feature.admin.inventory.domain.model.StockAdjustmentLog
import com.example.dentooth.feature.admin.inventory.domain.model.StockAdjustmentReason
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryUiState(
    val items: UiState<List<InventoryItem>> = UiState.Loading,
    val allItems: List<InventoryItem> = emptyList(),
    val lowStockItems: List<InventoryItem> = emptyList(),
    val logs: List<StockAdjustmentLog> = emptyList(),
    val selectedCategory: InventoryCategory? = null,
    val searchQuery: String = "",
    val onlyLowStockFilter: Boolean = false,
    val itemToRestock: InventoryItem? = null,
    val isAddItemOpen: Boolean = false,
    val errorMessage: String? = null
)

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            combine(
                inventoryRepository.getItems(),
                inventoryRepository.getLowStockItems(),
                inventoryRepository.getAdjustmentLogs()
            ) { items, lowStock, logs ->
                Triple(items, lowStock, logs)
            }.collect { (items, lowStock, logs) ->
                _uiState.update { current ->
                    current.copy(
                        allItems = items,
                        lowStockItems = lowStock,
                        logs = logs,
                        items = UiState.Success(filterItems(items, current))
                    )
                }
            }
        }
    }

    private fun filterItems(list: List<InventoryItem>, state: InventoryUiState): List<InventoryItem> {
        val q = state.searchQuery.trim().lowercase()
        return list.filter { item ->
            val matchCat = state.selectedCategory == null || item.category == state.selectedCategory
            val matchLow = !state.onlyLowStockFilter || item.isLowStock
            val matchQuery = q.isEmpty() ||
                    item.name.lowercase().contains(q) ||
                    item.supplierName.lowercase().contains(q)
            matchCat && matchLow && matchQuery
        }.sortedWith(compareBy({ !it.isLowStock }, { it.name }))
    }

    fun setSelectedCategory(category: InventoryCategory?) {
        _uiState.update { current ->
            val next = current.copy(selectedCategory = category)
            next.copy(items = UiState.Success(filterItems(next.allItems, next)))
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            val next = current.copy(searchQuery = query)
            next.copy(items = UiState.Success(filterItems(next.allItems, next)))
        }
    }

    fun toggleOnlyLowStock() {
        _uiState.update { current ->
            val next = current.copy(onlyLowStockFilter = !current.onlyLowStockFilter)
            next.copy(items = UiState.Success(filterItems(next.allItems, next)))
        }
    }

    fun openRestockDialog(item: InventoryItem) {
        _uiState.update { it.copy(itemToRestock = item) }
    }

    fun closeRestockDialog() {
        _uiState.update { it.copy(itemToRestock = null) }
    }

    fun restockItem(itemId: String, quantity: Int, unitCost: Double) {
        screenModelScope.launch {
            inventoryRepository.restockItem(itemId, quantity, unitCost)
            closeRestockDialog()
        }
    }

    fun adjustStock(itemId: String, delta: Int, reason: StockAdjustmentReason) {
        screenModelScope.launch {
            inventoryRepository.adjustStock(itemId, delta, reason)
        }
    }

    fun openAddItemDialog() {
        _uiState.update { it.copy(isAddItemOpen = true) }
    }

    fun closeAddItemDialog() {
        _uiState.update { it.copy(isAddItemOpen = false) }
    }

    fun addItem(
        name: String,
        category: InventoryCategory,
        stock: Int,
        minStock: Int,
        unitOfMeasure: String,
        unitCost: Double,
        supplierName: String,
        supplierPhone: String,
        notes: String?
    ) {
        screenModelScope.launch {
            val newItem = InventoryItem(
                id = "inv-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                name = name,
                category = category,
                currentStock = stock,
                minStockLevel = minStock,
                unitOfMeasure = unitOfMeasure,
                unitCost = unitCost,
                supplierName = supplierName,
                supplierPhone = supplierPhone,
                lastRestockedDate = DateTimeUtil.today().toString(),
                notes = notes
            )
            inventoryRepository.addItem(newItem)
            closeAddItemDialog()
        }
    }

    fun deleteItem(itemId: String) {
        screenModelScope.launch {
            inventoryRepository.deleteItem(itemId)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
