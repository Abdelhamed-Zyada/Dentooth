package com.example.dentooth.feature.billing.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class InvoiceStatus(val label: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF757575),
    SENT("Sent / Pending", 0xFF1976D2),
    PARTIALLY_PAID("Partially Paid", 0xFFFB8C00),
    PAID("Paid in Full", 0xFF2E7D32),
    OVERDUE("Overdue", 0xFFD32F2F),
    CANCELLED("Cancelled", 0xFF546E7A);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
enum class PaymentMethod(val label: String) {
    CASH("Cash (EGP)"),
    CREDIT_CARD("Credit / Debit Card (Visa/Mastercard)"),
    INSTAPAY("InstaPay Transfer"),
    VODAFONE_CASH("Vodafone / Mobile Wallet"),
    BANK_TRANSFER("Direct Bank Wire"),
    INSURANCE("Insurance Direct Billing")
}

@Serializable
enum class ExpenseCategory(val label: String, val colorHex: Long) {
    MATERIALS_SUPPLIES("Dental Materials & Supplies", 0xFF1976D2),
    LAB_FEES("Dental Lab & Prosthetics", 0xFF7B1FA2),
    RENT("Clinic Facility Rent", 0xFFE65100),
    UTILITIES("Utilities (Power/Water/Internet)", 0xFF00796B),
    STAFF_SALARY("Staff & Doctor Compensation", 0xFF388E3C),
    EQUIPMENT_MAINTENANCE("Equipment & Autoclave Maintenance", 0xFF5D4037),
    MARKETING("Marketing & Patient Acquisition", 0xFFC2185B),
    OTHER("Miscellaneous & Office Supplies", 0xFF616161);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
data class InvoiceItem(
    val id: String,
    val description: String,
    val quantity: Int = 1,
    val unitPrice: Double,
    val toothNumber: Int? = null,
    val procedureCode: String? = null
) {
    val totalPrice: Double
        get() = quantity * unitPrice
}

@Serializable
data class Invoice(
    val id: String,
    val invoiceNumber: String,
    val clinicId: String = "clinic-main",
    val branchId: String = "branch-main",
    val patientId: String,
    val patientName: String,
    val patientPhone: String = "",
    val doctorId: String? = null,
    val doctorName: String = "Dr. Ahmed Mostafa",
    val issueDate: String,    // YYYY-MM-DD
    val dueDate: String,      // YYYY-MM-DD
    val items: List<InvoiceItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxPercent: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val status: InvoiceStatus = InvoiceStatus.SENT,
    val notes: String? = null
) {
    val balance: Double
        get() = (totalAmount - paidAmount).coerceAtLeast(0.0)

    val isPaidInFull: Boolean
        get() = balance <= 0.001
}

@Serializable
data class Payment(
    val id: String,
    val invoiceId: String,
    val invoiceNumber: String,
    val patientId: String,
    val patientName: String,
    val amount: Double,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val transactionRef: String? = null,
    val paymentDate: String,   // YYYY-MM-DD
    val receiptNumber: String,
    val notes: String? = null
)

@Serializable
data class Expense(
    val id: String,
    val clinicId: String = "clinic-main",
    val branchId: String = "branch-main",
    val category: ExpenseCategory,
    val amount: Double,
    val description: String,
    val expenseDate: String,   // YYYY-MM-DD
    val paidTo: String,
    val receiptUrl: String? = null,
    val notes: String? = null
)

@Serializable
data class FinancialSummary(
    val totalBilled: Double = 0.0,
    val totalCollected: Double = 0.0,
    val outstandingBalance: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netIncome: Double = 0.0,
    val collectionRatePercent: Double = 0.0,
    val cashCollected: Double = 0.0,
    val electronicCollected: Double = 0.0,
    val insuranceCollected: Double = 0.0
)
