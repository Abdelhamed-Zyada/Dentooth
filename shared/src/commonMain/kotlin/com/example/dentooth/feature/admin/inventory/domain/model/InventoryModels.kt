package com.example.dentooth.feature.admin.inventory.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class InventoryCategory(val label: String, val colorHex: Long) {
    RESTORATIVE("Restorative & Composites", 0xFF1976D2),
    ENDODONTICS("Endodontics & Files", 0xFF7B1FA2),
    PROSTHETICS("Prosthetics & Lab", 0xFFE65100),
    SURGERY("Oral Surgery & Implants", 0xFF00796B),
    INFECTION_CONTROL("Sterilization & Hygiene", 0xFF388E3C),
    IMPRESSION("Impression Materials", 0xFF5D4037),
    DISPOSABLES("Disposables & PPE", 0xFF455A64);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
enum class StockLevelStatus(val label: String, val colorHex: Long) {
    IN_STOCK("In Stock", 0xFF2E7D32),
    LOW_STOCK("Low Stock", 0xFFFB8C00),
    CRITICAL("Critical - Reorder Now", 0xFFC62828);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
enum class StockAdjustmentReason(val label: String) {
    RESTOCK("Stock Delivery / Restock"),
    PROCEDURE_USAGE("Clinical Procedure Consumption"),
    EXPIRED("Expired Consumable"),
    DAMAGED("Damaged / Contaminated"),
    MANUAL_CORRECTION("Audit Correction")
}

@Serializable
data class InventoryItem(
    val id: String,
    val clinicId: String = "clinic-main",
    val name: String,
    val category: InventoryCategory,
    val currentStock: Int,
    val minStockLevel: Int, // Reorder trigger threshold
    val unitOfMeasure: String, // e.g. "Boxes", "Tubes", "Carpules", "Bottles"
    val unitCost: Double, // in EGP
    val supplierName: String,
    val supplierPhone: String = "",
    val lastRestockedDate: String, // YYYY-MM-DD
    val notes: String? = null
) {
    val status: StockLevelStatus
        get() = when {
            currentStock <= minStockLevel / 2 -> StockLevelStatus.CRITICAL
            currentStock <= minStockLevel -> StockLevelStatus.LOW_STOCK
            else -> StockLevelStatus.IN_STOCK
        }

    val isLowStock: Boolean
        get() = currentStock <= minStockLevel

    val totalValue: Double
        get() = currentStock * unitCost
}

@Serializable
data class StockAdjustmentLog(
    val id: String,
    val itemId: String,
    val itemName: String,
    val changeQty: Int,
    val reason: StockAdjustmentReason,
    val date: String,
    val adjustedBy: String = "Clinic Staff"
)
