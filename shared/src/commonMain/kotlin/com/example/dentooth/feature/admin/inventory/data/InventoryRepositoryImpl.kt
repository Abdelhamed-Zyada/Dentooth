package com.example.dentooth.feature.admin.inventory.data

import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.admin.inventory.domain.model.InventoryCategory
import com.example.dentooth.feature.admin.inventory.domain.model.InventoryItem
import com.example.dentooth.feature.admin.inventory.domain.model.StockAdjustmentLog
import com.example.dentooth.feature.admin.inventory.domain.model.StockAdjustmentReason
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class InventoryRepositoryImpl : InventoryRepository {

    private val _items = MutableStateFlow<List<InventoryItem>>(emptyList())
    private val _logs = MutableStateFlow<List<StockAdjustmentLog>>(emptyList())

    init {
        seedInitialInventory()
    }

    private fun seedInitialInventory() {
        val todayStr = DateTimeUtil.today().toString()

        val sampleItems = listOf(
            InventoryItem(
                id = "inv-1",
                name = "3M Filtek Z250 Universal Composite (Shade A2)",
                category = InventoryCategory.RESTORATIVE,
                currentStock = 3,
                minStockLevel = 5,
                unitOfMeasure = "Syringes (4g)",
                unitCost = 650.0,
                supplierName = "Dental Supplies Egypt Co.",
                supplierPhone = "+201004829102",
                lastRestockedDate = "2026-11-01",
                notes = "High-demand composite shade"
            ),
            InventoryItem(
                id = "inv-2",
                name = "Septodont Articaine 4% with Epinephrine 1:100,000",
                category = InventoryCategory.SURGERY,
                currentStock = 12,
                minStockLevel = 10,
                unitOfMeasure = "Boxes (50 Carpules)",
                unitCost = 820.0,
                supplierName = "PharmaDent Distribution",
                supplierPhone = "+201201938472",
                lastRestockedDate = "2026-11-08",
                notes = "Local anesthesia for surgical & endo procedures"
            ),
            InventoryItem(
                id = "inv-3",
                name = "Single Bond Universal Dental Adhesive 5ml",
                category = InventoryCategory.RESTORATIVE,
                currentStock = 1,
                minStockLevel = 3,
                unitOfMeasure = "Bottles (5ml)",
                unitCost = 980.0,
                supplierName = "Dental Supplies Egypt Co.",
                supplierPhone = "+201004829102",
                lastRestockedDate = "2026-10-15",
                notes = "Critical stock level! Needs immediate reorder"
            ),
            InventoryItem(
                id = "inv-4",
                name = "Dentsply Protaper Gold Rotary Endodontic Files",
                category = InventoryCategory.ENDODONTICS,
                currentStock = 7,
                minStockLevel = 4,
                unitOfMeasure = "Blister Packs (6 Files)",
                unitCost = 550.0,
                supplierName = "EndoTech Cairo",
                supplierPhone = "+201112837465",
                lastRestockedDate = "2026-11-12",
                notes = "SX-F3 assortment pack"
            ),
            InventoryItem(
                id = "inv-5",
                name = "Cavex ColorChange Dust-Free Alginate 500g",
                category = InventoryCategory.IMPRESSION,
                currentStock = 2,
                minStockLevel = 4,
                unitOfMeasure = "Bags (500g)",
                unitCost = 280.0,
                supplierName = "Al-Ahram Dental Supplies",
                supplierPhone = "+201098237412",
                lastRestockedDate = "2026-10-28",
                notes = "Fast-setting orthodontic & study model impression"
            ),
            InventoryItem(
                id = "inv-6",
                name = "Nitrile Powder-Free Medical Examination Gloves (Medium)",
                category = InventoryCategory.DISPOSABLES,
                currentStock = 18,
                minStockLevel = 8,
                unitOfMeasure = "Boxes (100 Gloves)",
                unitCost = 210.0,
                supplierName = "Hygieia Medical Supplies",
                supplierPhone = "+201223948571",
                lastRestockedDate = "2026-11-10",
                notes = "Latex-free examination gloves"
            ),
            InventoryItem(
                id = "inv-7",
                name = "Patient Bibs 3-Ply Waterproof Barrier",
                category = InventoryCategory.DISPOSABLES,
                currentStock = 25,
                minStockLevel = 10,
                unitOfMeasure = "Packs (125 Bibs)",
                unitCost = 140.0,
                supplierName = "Hygieia Medical Supplies",
                supplierPhone = "+201223948571",
                lastRestockedDate = "2026-11-05",
                notes = "Standard blue clinic bibs"
            ),
            InventoryItem(
                id = "inv-8",
                name = "Swann Morton Sterile Carbon Steel Scalpel Blades #15",
                category = InventoryCategory.SURGERY,
                currentStock = 3,
                minStockLevel = 5,
                unitOfMeasure = "Boxes (100 Blades)",
                unitCost = 360.0,
                supplierName = "PharmaDent Distribution",
                supplierPhone = "+201201938472",
                lastRestockedDate = "2026-10-20",
                notes = "Surgical extraction and periodontal flap surgery"
            )
        )

        val sampleLogs = listOf(
            StockAdjustmentLog(
                id = "log-1",
                itemId = "inv-2",
                itemName = "Septodont Articaine 4% with Epinephrine",
                changeQty = 10,
                reason = StockAdjustmentReason.RESTOCK,
                date = "2026-11-08",
                adjustedBy = "Nour El-Din (Sterilization Tech)"
            ),
            StockAdjustmentLog(
                id = "log-2",
                itemId = "inv-3",
                itemName = "Single Bond Universal Dental Adhesive 5ml",
                changeQty = -1,
                reason = StockAdjustmentReason.PROCEDURE_USAGE,
                date = "2026-11-15",
                adjustedBy = "Dr. Ahmed Mostafa"
            )
        )

        _items.value = sampleItems
        _logs.value = sampleLogs
    }

    override fun getItems(): Flow<List<InventoryItem>> = _items.asStateFlow()

    override fun getLowStockItems(): Flow<List<InventoryItem>> {
        return _items.map { list -> list.filter { it.isLowStock } }
    }

    override fun getAdjustmentLogs(): Flow<List<StockAdjustmentLog>> = _logs.asStateFlow()

    override suspend fun restockItem(itemId: String, quantity: Int, cost: Double): Result<Unit> {
        val current = _items.value
        val index = current.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val item = current[index]
            val updatedItem = item.copy(
                currentStock = item.currentStock + quantity,
                unitCost = if (cost > 0) cost else item.unitCost,
                lastRestockedDate = DateTimeUtil.today().toString()
            )
            val updated = current.toMutableList()
            updated[index] = updatedItem
            _items.value = updated

            val newLog = StockAdjustmentLog(
                id = "log-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                itemId = item.id,
                itemName = item.name,
                changeQty = quantity,
                reason = StockAdjustmentReason.RESTOCK,
                date = DateTimeUtil.today().toString()
            )
            _logs.value = listOf(newLog) + _logs.value

            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Item not found"))
    }

    override suspend fun adjustStock(itemId: String, delta: Int, reason: StockAdjustmentReason): Result<Unit> {
        val current = _items.value
        val index = current.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val item = current[index]
            val newStock = (item.currentStock + delta).coerceAtLeast(0)
            val updatedItem = item.copy(currentStock = newStock)
            val updated = current.toMutableList()
            updated[index] = updatedItem
            _items.value = updated

            val newLog = StockAdjustmentLog(
                id = "log-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                itemId = item.id,
                itemName = item.name,
                changeQty = delta,
                reason = reason,
                date = DateTimeUtil.today().toString()
            )
            _logs.value = listOf(newLog) + _logs.value

            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Item not found"))
    }

    override suspend fun addItem(item: InventoryItem): Result<InventoryItem> {
        _items.value = listOf(item) + _items.value
        return Result.success(item)
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> {
        _items.value = _items.value.filter { it.id != itemId }
        return Result.success(Unit)
    }
}
