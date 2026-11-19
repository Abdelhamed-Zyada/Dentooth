package com.example.dentooth.feature.admin.inventory.data

import com.example.dentooth.feature.admin.inventory.domain.model.InventoryItem
import com.example.dentooth.feature.admin.inventory.domain.model.StockAdjustmentLog
import com.example.dentooth.feature.admin.inventory.domain.model.StockAdjustmentReason
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getItems(): Flow<List<InventoryItem>>
    fun getLowStockItems(): Flow<List<InventoryItem>>
    fun getAdjustmentLogs(): Flow<List<StockAdjustmentLog>>
    suspend fun restockItem(itemId: String, quantity: Int, cost: Double): Result<Unit>
    suspend fun adjustStock(itemId: String, delta: Int, reason: StockAdjustmentReason): Result<Unit>
    suspend fun addItem(item: InventoryItem): Result<InventoryItem>
    suspend fun deleteItem(itemId: String): Result<Unit>
}
